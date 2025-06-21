package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

  // do I need a connection here?
  public MySqlCategoryDao(DataSource dataSource) {
    super(dataSource);
  }

  // get all categories
  @Override
  public List<Category> getAllCategories() {
    List<Category> categories = new ArrayList<>();
    String query = "SELECT * FROM categories";
    try (PreparedStatement ps = getConnection().prepareStatement(query)) {
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        categories.add(
            new Category(
                rs.getInt("category_id"), rs.getString("name"), rs.getString("description")));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return categories;
  }

  @Override
  public Category getById(int categoryId) {
    // get category by id
    String query = "SELECT * FROM categories WHERE categories.category_id = ?";
    try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
      preparedStatement.setInt(1, categoryId);
      ResultSet rs = preparedStatement.executeQuery();
      if (!rs.next()) {
        return null;
      }
      return new Category(
          rs.getInt("category_id"), rs.getString("name"), rs.getString("description"));

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Category create(Category category) {
    // create a new category
    String query = "INSERT INTO categories ( name, description) VALUES (?, ?)";
    try (Connection connection = getConnection()) {
      PreparedStatement preparedStatement = getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, category.getName());
      preparedStatement.setString(2, category.getDescription());
      int rowsAffected = preparedStatement.executeUpdate();

      if (rowsAffected > 0) {
        // Retrieve the generated keys
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

        if (generatedKeys.next()) {
          // Retrieve the auto-incremented ID
          int orderId = generatedKeys.getInt(1);

          // get the newly inserted category
          return getById(orderId);
        }
      }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    return null;
  }

  @Override
  public void update(int categoryId, Category category) {
    // update category

    String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

    try (PreparedStatement ps = getConnection().prepareStatement(query)) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getDescription());
      ps.setInt(3, categoryId);
      ps.executeUpdate();
    } catch (SQLException e) {
      System.err.println("Update Failed - SQL Exception: " + e.getMessage());
    }
  }

  @Override
  public void delete(int categoryId) {
    // delete category
    String query = "DELETE FROM categories WHERE category_id = ?";
    try (PreparedStatement ps = getConnection().prepareStatement(query)) {
      ps.setInt(1, categoryId);
      ps.executeUpdate();
    } catch (SQLException e) {
      System.err.println("Delete Category failed - SQL Exception: " + e.getMessage());
    }
  }

  // this isnt being used but in product it is, maybe i need to fix something so it is being used
  private Category mapRow(ResultSet row) throws SQLException {
    int categoryId = row.getInt("category_id");
    String name = row.getString("name");
    String description = row.getString("description");

    Category category =
        new Category() {
          {
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
          }
        };

    return category;
  }
}
