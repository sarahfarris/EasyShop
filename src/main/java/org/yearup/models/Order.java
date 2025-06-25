package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private LocalDateTime date;
    private String address;
    private String city;
    private String state;
    private String zip;
    private BigDecimal shippingAmount = null;
    private BigDecimal total = BigDecimal.valueOf(-1);
    private List<OrderLineItem> lineItems = new ArrayList<>();

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BigDecimal getShippingAmount() {
        if (shippingAmount == null) {
            calculateShippingAmount();
        }
        return shippingAmount;
    }

    public void setShippingAmount() {
        calculateShippingAmount();
    }


    public BigDecimal getTotal() {
        if (total.equals(BigDecimal.valueOf(-1))) {
            calculateTotal();
        }
        return total;
    }

    public void calculateTotal() {
        this.total = lineItems
                .stream()
                .map(OrderLineItem::getTotal)
                .reduce( BigDecimal.ZERO, (lineTotal, subTotal) -> subTotal.add(lineTotal));

    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    // Calculates shipping amount by state based on their sales tax.
    private void calculateShippingAmount() {
        if (state == null || state.trim().isEmpty()) {
            System.err.println("State cannot be null or empty. Returning 0 tax.");
            shippingAmount = BigDecimal.ZERO;
        }

        switch (state) {
            case "CA" -> this.shippingAmount = new BigDecimal("0.0725"); // California sales tax rate (example)
            case "NY" ->
                    this.shippingAmount = new BigDecimal("0.0400"); // New York state sales tax rate (base, cities add more)
            case "TX" -> this.shippingAmount = new BigDecimal("0.0625"); // Texas sales tax rate
            case "FL" -> this.shippingAmount = new BigDecimal("0.0600"); // Florida sales tax rate
            case "WA" -> this.shippingAmount = new BigDecimal("0.0650"); // Washington sales tax rate
            case "OR", "DE", "NH", "MT", "AK" ->
                // States with no statewide sales tax (e.g., Oregon, Delaware, New Hampshire, Montana, Alaska)
                    this.shippingAmount = BigDecimal.ZERO;
            default -> {
                // Default case for states not explicitly listed
                System.out.println("Sales tax rate for state " + state + " not found. Returning default 0 tax.");
                this.shippingAmount = BigDecimal.ZERO; // Default to 0 or handle as an error/unknown state
            }
        }
    }
}
