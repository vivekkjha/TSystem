package com.vivek.tsystem.common.error;

/**
 * Created by vivek on 13/08/17.
 */

public class ArgsRequiredException extends BaseException {

    private static final String MESSAGE = "Argument in bundle required to perform operation";
    public ArgsRequiredException() {

    }

    public ArgsRequiredException(String message) {
        super(MESSAGE + " : "+ message);
    }

    public ArgsRequiredException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}
