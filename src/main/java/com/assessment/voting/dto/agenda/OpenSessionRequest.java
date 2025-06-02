package com.assessment.voting.dto.agenda;

import com.assessment.voting.model.enumType.TimeUnitEnum;
import com.assessment.voting.util.ValidEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OpenSessionRequest(@NotNull Long agendaId,
                                 @ValidEnum(enumClass = TimeUnitEnum.class)
                                 String timeUnit,
                                 @Positive Integer quantity) {
}
