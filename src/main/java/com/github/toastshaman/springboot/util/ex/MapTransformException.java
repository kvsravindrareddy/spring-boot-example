package com.github.toastshaman.springboot.util.ex;

/** Something has gone wrong during a MapTransformer transform */
public abstract class MapTransformException extends InvalidDataStateException {

    protected MapTransformException(String message) {
        super(message);
    }

    protected MapTransformException(String message, Throwable cause) {
        super(message, cause);
    }
}
