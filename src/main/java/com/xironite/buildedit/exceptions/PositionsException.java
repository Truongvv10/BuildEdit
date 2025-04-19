package com.xironite.buildedit.exceptions;

public class PositionsException extends RuntimeException {

    public PositionsException() {
        super("Invalid positions selected.");
    }

    public PositionsException(String message) {
        super(message);
    }

    public PositionsException(String message, Throwable cause) {
        super(message, cause);
    }

}
