package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;

public interface OrderDao {
    Order createOrder(Order order);
    OrderLineItem addOrderLineItem(OrderLineItem orderLineItem);
}
