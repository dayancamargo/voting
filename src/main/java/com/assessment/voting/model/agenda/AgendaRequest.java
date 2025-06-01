package com.assessment.voting.model.agenda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendaRequest(Long id, @NotNull @Size(max = 100) String name) {
}
