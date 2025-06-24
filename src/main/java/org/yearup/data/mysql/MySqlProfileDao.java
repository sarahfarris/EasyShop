package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{

    /**
     * Creates profile
     * @param dataSource uses the datasource to connect to database to add profile
     */
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a user profile from the database by their user ID.
     * @param userId The ID of the user whose profile is to be retrieved.
     * @return The Profile object if found, or null if no profile exists for the given user ID.
     */
    @Override // Assuming this method is part of a ProfileDao interface
    public Profile getByUserId(int userId) {
        Profile profile = null; // Initialize profile to null
        String sql = "SELECT user_id, first_name, last_name, phone, email, address, city, state, zip " +
                "FROM profiles " +
                "WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId); // Set the user_id parameter

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) { // Check if a row was returned
                    // Extract data from the ResultSet and populate the Profile object
                    profile = new Profile(
                            resultSet.getInt("user_id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                            resultSet.getString("city"),
                            resultSet.getString("state"),
                            resultSet.getString("zip")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving profile", e);
        }

        return profile;
    }

    @Override
    public Profile updateProfile(int userId, Profile profile) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE profiles SET ");
        List<String> params = new ArrayList<>();

        boolean firstField = true;

        if (profile.getFirstName() != null && !profile.getFirstName().isEmpty()) {
            sqlBuilder.append("first_name = ?");
            params.add(profile.getFirstName());
            firstField = false;
        }
        if (profile.getLastName() != null && !profile.getLastName().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("last_name = ?");
            params.add(profile.getLastName());
            firstField = false;
        }
        if (profile.getPhone() != null && !profile.getPhone().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("phone = ?");
            params.add(profile.getPhone());
            firstField = false;
        }
        if (profile.getEmail() != null && !profile.getEmail().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("email = ?");
            params.add(profile.getEmail());
            firstField = false;
        }
        if (profile.getAddress() != null && !profile.getAddress().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("address = ?");
            params.add(profile.getAddress());
            firstField = false;
        }
        if (profile.getCity() != null && !profile.getCity().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("city = ?");
            params.add(profile.getCity());
            firstField = false;
        }
        if (profile.getState() != null && !profile.getState().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("state = ?");
            params.add(profile.getState());
            firstField = false;
        }
        if (profile.getZip() != null && !profile.getZip().isEmpty()) {
            if (!firstField) sqlBuilder.append(", ");
            sqlBuilder.append("zip = ?");
            params.add(profile.getZip());
        }

        if (params.isEmpty()) {
            // No fields were provided to update, so nothing to do in the DB.
            // Return the existing profile (or null, depending on API's contract)
            System.out.println("No update fields provided for user ID: " + userId + ". No database operation performed.");
            return getByUserId(userId); // Or throw new IllegalArgumentException("No fields to update");
        }

        sqlBuilder.append(" WHERE user_id = ?"); // Add the WHERE clause

        String sql = sqlBuilder.toString(); // Final SQL string

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Bind parameters to the PreparedStatement dynamically
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i)); // setObject handles various types
            }
            ps.setInt(params.size() + 1, userId);

            int rowsAffected = ps.executeUpdate();

            // this should not be possible but adding a case just in case
            if (rowsAffected == 0) {
                System.out.println("User doesn't exist in database, aborting");
                return null;
            }

            return getByUserId(userId); // Return the updated profile from the database
        } catch (SQLException e) {
            throw new RuntimeException("Error updating profile", e);
        }
    }

}
