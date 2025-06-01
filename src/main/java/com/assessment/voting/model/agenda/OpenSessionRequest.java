package com.assessment.voting.model.agenda;

import com.assessment.voting.model.TimeUnitEnum;
import com.assessment.voting.util.ValidEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OpenSessionRequest(@NotNull Long agendaId,
                                 @ValidEnum(enumClass = TimeUnitEnum.class)
                                 String timeUnit,
                                 @Positive Integer quantity) {
}
