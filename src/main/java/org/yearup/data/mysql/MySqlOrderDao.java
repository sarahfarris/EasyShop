package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Adds new orders to database
 */

@Component
public class MySqlOrderDao  extends MySqlDaoBase implements OrderDao {
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Adds the order to database, gathers information needed for order
     * @param order object that contains order information
     */
    @Override
    public Order createOrder(Order order) {
        String sql = """
                INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(order.getDate()));
            ps.setString(3, order.getAddress());
            ps.setString(4, order.getCity());
            ps.setString(5, order.getState());
            ps.setString(6, order.getZip());
            ps.setBigDecimal(7, order.getShippingAmount());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = ps.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    order.setOrderId(orderId);
                    return order;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    /**
     * Save item to order and add to database
     * @param item to be added to the order
     */

    @Override
    public OrderLineItem addOrderLineItem(OrderLineItem item) {
        String sql = """
                INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getProductId());
            ps.setBigDecimal(3, item.getSalesPrice());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getDiscountPercent());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = ps.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    item.setOrderLineItemId(orderId);
                    return item;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error creating line item", ex);
        }
        return item;
    }
}
