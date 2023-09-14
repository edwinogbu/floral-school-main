package com.backend.floralschoolmain.exception;

public class UserAreadyExistException extends RuntimeException{
    public UserAreadyExistException(String message) {
        super(message);
    }
}
