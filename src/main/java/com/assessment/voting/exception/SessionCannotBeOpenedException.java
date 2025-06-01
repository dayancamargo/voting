package com.assessment.voting.exception;

public class SessionCannotBeOpenedException extends BusinessException {
    public SessionCannotBeOpenedException(String message) {
        super(message);
    }
}
