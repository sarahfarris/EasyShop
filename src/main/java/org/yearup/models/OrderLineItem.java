package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderLineItem {
    private int orderLineItemId;
    private int orderId;
    private int productId;
    private Product product;
    private BigDecimal salesPrice;
    private int quantity;
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private LocalDateTime date;

    public int getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(int orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getTotal()
    {
        float subTotal = salesPrice.floatValue() * quantity;
        if (discountPercent == null || discountPercent.equals(BigDecimal.ZERO)) return BigDecimal.valueOf(subTotal);
        return BigDecimal.valueOf(subTotal - ((subTotal * discountPercent.intValueExact()) / 100));
    }
}