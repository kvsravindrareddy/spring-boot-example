package com.github.toastshaman.springboot.util.ex;

import com.google.common.base.Joiner;
import com.github.toastshaman.springboot.util.MapTransformer;

/** Field value is invalid */
public class InvalidFieldException extends MapTransformException {

    public InvalidFieldException(String message) {
        super(message);
    }

    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldException(Class<? extends MapTransformer> origin,
                                 String fieldName,
                                 Throwable cause) {
        this(Joiner.on(": ").skipNulls()
            .join(origin.getSimpleName() + ": " + fieldName + " invalid", cause.getMessage()), cause);
    }
}
