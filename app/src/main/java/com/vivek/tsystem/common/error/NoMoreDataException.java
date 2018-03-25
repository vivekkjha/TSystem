package com.vivek.tsystem.common.error;

/**
 * Created by vivek on 05/01/18.
 */

public class NoMoreDataException extends BaseException {

    private static final String MESSAGE = "No More data not available";
    public NoMoreDataException() {

    }

    public NoMoreDataException(String message) {
        super(MESSAGE + " : "+ message);
    }

    public NoMoreDataException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}
