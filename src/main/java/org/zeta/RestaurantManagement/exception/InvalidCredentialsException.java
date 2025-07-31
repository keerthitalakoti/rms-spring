package org.zeta.RestaurantManagement.exception;
public class InvalidCredentialsException extends BadRequestException {
    public InvalidCredentialsException(String message) { super(message); }
}