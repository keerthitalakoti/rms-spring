package org.zeta.RestaurantManagement.exception;
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) { super(message); }
}