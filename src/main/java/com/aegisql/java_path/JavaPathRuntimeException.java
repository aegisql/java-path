package com.aegisql.java_path;

/**
 * The type Java path runtime exception.
 */
public class JavaPathRuntimeException extends RuntimeException {
    /**
     * Instantiates a new Java path runtime exception.
     */
    public JavaPathRuntimeException() {
    }

    /**
     * Instantiates a new Java path runtime exception.
     *
     * @param message the message
     */
    public JavaPathRuntimeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Java path runtime exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JavaPathRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Java path runtime exception.
     *
     * @param cause the cause
     */
    public JavaPathRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Java path runtime exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public JavaPathRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
