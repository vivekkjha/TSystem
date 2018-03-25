package com.vivek.tsystem.common.error;

/**
 * Created by vivekjha on 05/12/16.
 */

public class BaseException extends RuntimeException {
    /**
     * Constructs a new {@code RuntimeException} that includes the current stack
     * trace.
     */
    public BaseException() {
    }

    /**
     * Constructs a new {@code RuntimeException} with the current stack trace
     * and the specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public BaseException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code RuntimeException} with the current stack trace,
     * the specified detail message and the specified cause.
     *
     * @param detailMessage the detail message for this exception.
     * @param throwable
     */
    public BaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@code RuntimeException} with the current stack trace
     * and the specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public BaseException(Throwable throwable) {
        super(throwable);
    }
}
