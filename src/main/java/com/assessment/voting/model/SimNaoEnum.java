package com.assessment.voting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SimNaoEnum {
    SIM,
    NAO;

    public static SimNaoEnum fromString(String value) {
        for (SimNaoEnum unit : SimNaoEnum.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown time unit: " + value);
    }
}
