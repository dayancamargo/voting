package com.assessment.voting.dto.agenda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendaRequest(Long id, @NotNull @Size(max = 100) String name) {
}
