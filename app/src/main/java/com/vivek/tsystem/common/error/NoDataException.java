package com.vivek.tsystem.common.error;

/**
 * Created by vivek on 05/01/18.
 */

public class NoDataException extends BaseException {

    private static final String MESSAGE = "Required data not available";
    public NoDataException() {

    }

    public NoDataException(String message) {
        super(MESSAGE + " : "+ message);
    }

    public NoDataException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}
