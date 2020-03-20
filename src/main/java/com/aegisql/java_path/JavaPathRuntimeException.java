package com.aegisql.java_path;

public class JavaPathRuntimeException extends RuntimeException {
    public JavaPathRuntimeException() {
    }

    public JavaPathRuntimeException(String message) {
        super(message);
    }

    public JavaPathRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaPathRuntimeException(Throwable cause) {
        super(cause);
    }

    public JavaPathRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
