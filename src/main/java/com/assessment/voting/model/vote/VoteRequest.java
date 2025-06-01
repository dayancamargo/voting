package com.assessment.voting.model.vote;

import com.assessment.voting.model.SimNaoEnum;
import com.assessment.voting.util.ValidEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VoteRequest(@NotNull
                          Long agenda,
                          @NotNull
                          @ValidEnum(enumClass = SimNaoEnum.class)
                          String answer,
                          @NotNull
                          @Size(min = 11, max = 11, message = "CPF must be exactly 11 characters long")
                          String cpf) {
}
