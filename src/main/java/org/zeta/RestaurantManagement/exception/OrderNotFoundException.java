package org.zeta.RestaurantManagement.exception;
public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) { super(message); }
}
