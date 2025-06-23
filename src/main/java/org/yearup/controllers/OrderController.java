package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderDao orderDao;
    private final ShoppingCartDao cartDao;
    private final UserDao userDao;
    private final ProfileDao profileDao;

    @Autowired
    public OrderController(OrderDao orderDao, ShoppingCartDao cartDao, UserDao userDao, ProfileDao profileDao) {
        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(Principal principal) {
        int userId = getUserId(principal);
        ShoppingCart cart = cartDao.getByUserId(userId);
        if (cart.getItems().isEmpty()) return null;
        Profile profile = profileDao.getByUserId(userId);
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        if (profile != null) {
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
        }
        // Calculates shipping price based on state
        order.setShippingAmount();
        orderDao.createOrder(order);

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            OrderLineItem item = new OrderLineItem();
            item.setOrderId(order.getOrderId());
            item.setProductId(cartItem.getProductId());
            item.setSalesPrice(cartItem.getProduct().getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setDiscountPercent(java.math.BigDecimal.ZERO);
            item.setDate(LocalDateTime.now());
            orderLineItems.add(item);
            orderDao.addOrderLineItem(item);
        }
        order.setItems(orderLineItems);
        order.calculateTotal();
        cartDao.emptyCart(userId);
        return order;
    }

    private int getUserId(Principal principal) {
        // get the currently logged in username
        String userName = principal.getName();
        // find database user by userId
        User user = userDao.getByUserName(userName);
        return user.getId();
    }
}
