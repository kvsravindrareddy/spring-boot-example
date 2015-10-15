package com.github.toastshaman.springboot.util.ex;

/** Field value is null when the transformer entry implies it should never be */
public class FieldRequiredException extends MapTransformException {

    public FieldRequiredException(String message) {
        super(message);
    }

    public FieldRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
