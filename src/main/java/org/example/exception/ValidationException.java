package org.example.exception;

import org.example.domain.Point;

public class ValidationException extends RuntimeException {
    public ValidationException(Point p) {
        super(String.format("Object %s is not valid", p));
    }
}
