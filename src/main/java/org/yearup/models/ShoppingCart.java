package org.yearup.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart
{
    public ShoppingCart() {
        items = new HashMap<>();
    }

    private Map<Integer, ShoppingCartItem> items;
    private BigDecimal total;

    public Map<Integer, ShoppingCartItem> getItems()
    {
        return items;
    }

    public void setItems(Map<Integer, ShoppingCartItem> items)
    {
        this.items = items;
    }

    public boolean contains(int productId)
    {
        return items.containsKey(productId);
    }

    public void add(ShoppingCartItem item)
    {
        items.put(item.getProductId(), item);
    }

    public ShoppingCartItem get(int productId)
    {
        return items.get(productId);
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public void calculateTotal() {
        total = items.values()
                .stream()
                .map(i -> i.getLineTotal())
                .reduce( BigDecimal.ZERO, (lineTotal, subTotal) -> subTotal.add(lineTotal));
    }

}
