package com.assessment.voting.dto.vote;

import com.assessment.voting.model.enumType.SimNaoEnum;
import com.assessment.voting.util.ValidEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VoteMobileRequest(@NotNull
                                Long idAgenda,
                                @NotNull
                                @ValidEnum(enumClass = SimNaoEnum.class)
                                String idVoto,
                                @NotNull
                                @Size(min = 11, max = 11, message = "CPF must be exactly 11 characters long")
                                String idCpf) {
}
