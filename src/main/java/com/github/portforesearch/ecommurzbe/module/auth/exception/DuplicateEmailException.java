package com.github.portforesearch.ecommurzbe.module.auth.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
