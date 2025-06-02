package com.assessment.voting.dto.agenda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendaMobileRequest(@NotNull @Size(max = 100) String nome) {
}

