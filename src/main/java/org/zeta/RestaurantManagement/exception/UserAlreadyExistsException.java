package org.zeta.RestaurantManagement.exception;
public class UserAlreadyExistsException extends BadRequestException {
    public UserAlreadyExistsException(String message) { super(message); }
}
