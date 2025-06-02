package com.assessment.voting.model.enumType;

public enum ScreenTypeEnum {
    FORMULARIO,
    SELECAO;

    public static ScreenTypeEnum fromString(String value) {
        for (ScreenTypeEnum type : ScreenTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown screen type: " + value);
    }
}
