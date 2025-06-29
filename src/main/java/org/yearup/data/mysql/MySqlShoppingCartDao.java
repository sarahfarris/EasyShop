package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private final ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    /**
     * Retrieves shopping cart based on user and user authentication
     * @param userId identifies and authenticates the user
     * @return shopping cart
     */

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String sql = "SELECT * FROM shopping_cart " +
                " WHERE user_id = ? ";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

            while (row.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(productDao.getById(row.getInt("product_id")));
                item.setQuantity(row.getInt("quantity"));
                cart.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        cart.calculateTotal();
        return cart;
    }

    /**
     * Allows user to modify cart (positive additions)
     * @param userId authenticates user
     * @param productId to add product or modify quantity (updateQuantity())
     */
    @Override
    public void addOrUpdate(int userId, int productId) {
        ShoppingCart shoppingCart = getByUserId(userId);
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)";

        try (Connection connection = getConnection()) {
            if (shoppingCart.get(productId) == null) {
                // add to shopping cart
                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, productId);
                insertStatement.executeUpdate();
            } else {
                // update shopping cart and add 1
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setInt(1, userId);
                updateStatement.setInt(2, productId);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            removeItem(userId, productId);
        } else {
            String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setInt(1, quantity);
                statement.setInt(2, userId);
                statement.setInt(3, productId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    // This indicates that no row matching user_id and product_id was found.
                    // You might want to log this, or throw a more specific exception
                    // (e.g., CartItemNotFoundException, which could map to HTTP 404).
                    System.out.println("No matching cart item found for update: user=" + userId + ", product=" + productId);
                } else {
                    System.out.println("Cart item updated. Rows affected: " + rowsAffected);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Empty entire cart, or remove one item (removeItem())
     * @param userId authenticates user
     */

    @Override
    public void emptyCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeItem(int userId, int productId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
