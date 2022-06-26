package org.silentsoft.solarguard.exception;

public class LoginTokenNotFoundException extends RuntimeException {
    public LoginTokenNotFoundException(String message) {
        super(message);
    }
}
