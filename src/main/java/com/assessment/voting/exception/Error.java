package com.assessment.voting.exception;

import java.util.Map;

public record Error(String message,
                    Integer status,
                    String path,
                    String method,
                    Map<String, String> fields) {
}
