package com.github.toastshaman.springboot.util.ex;

/** Something has gone wrong during a MapTransformer transform */
public abstract class InvalidDataStateException extends RuntimeException {

    protected InvalidDataStateException() {
        super();
    }

    protected InvalidDataStateException(String message) {
        super(message);
    }

    protected InvalidDataStateException(String message, Throwable cause) {
        super(message, cause);
    }

    protected InvalidDataStateException(Throwable cause) {
        super(cause);
    }

    protected InvalidDataStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
