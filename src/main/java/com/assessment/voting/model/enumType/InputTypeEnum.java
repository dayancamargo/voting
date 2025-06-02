package com.assessment.voting.model.enumType;

public enum InputTypeEnum {
    INPUT_TEXTO,
    INPUT_NUMERO,
    INPUT_DATA,
    TEXTO,
    OUTRO;

    public static InputTypeEnum fromString(String value) {
        for (InputTypeEnum type : InputTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return OUTRO;
    }
}
