package com.assessment.voting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeUnitEnum {
    SECONDS,
    MINUTES,
    HOURS,
    DAYS;

    public static TimeUnitEnum fromString(String value) {
        for (TimeUnitEnum unit : TimeUnitEnum.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown time unit: " + value);
    }
}