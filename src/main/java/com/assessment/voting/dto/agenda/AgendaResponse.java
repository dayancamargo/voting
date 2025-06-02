package com.assessment.voting.dto.agenda;

import java.time.LocalDateTime;

public record AgendaResponse(Long id, String name, LocalDateTime startTime, LocalDateTime endTime) {
    public boolean isOpened() {
        return startTime != null && endTime != null && LocalDateTime.now().isAfter(startTime) && LocalDateTime.now().isBefore(endTime);
    }
}
