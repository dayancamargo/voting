package com.assessment.voting.exception;

public class SessionNotOpenedException extends BusinessException {
    public SessionNotOpenedException(String message) {
        super(message);
    }
}
