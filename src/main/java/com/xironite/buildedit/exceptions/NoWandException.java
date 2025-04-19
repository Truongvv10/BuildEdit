package com.xironite.buildedit.exceptions;

public class NoWandException extends RuntimeException {

    public NoWandException() {
        super("You must be holding a valid wand to use this command.");
    }

    public NoWandException(String message) {
        super(message);
    }

    public NoWandException(String message, Throwable cause) {
        super(message, cause);
    }

}
