package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    void addOrUpdate(int userId, int productId);
    void emptyCart(int userId);
    void updateQuantity(int userId, int productId, int quantity);
}
