package com.assessment.voting.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class EnumValidatorImpl implements ConstraintValidator<ValidEnum, String> {

    private final List<String> valueList = new ArrayList<>();
    private boolean ignoreCase = true;
    private String defaultMessage = "";

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        for (Enum<?> enumConstant : constraintAnnotation.enumClass().getEnumConstants()) {
            valueList.add(enumConstant.name());
        }
        ignoreCase = constraintAnnotation.ignoreCase();
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean result = ignoreCase
                ? valueList.stream().map(String::toLowerCase).anyMatch(v -> v.equals(value.toLowerCase()))
                : valueList.contains(value);

        if (!result) {

            if (defaultMessage != null && !defaultMessage.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(defaultMessage)
                        .addConstraintViolation();
                return false;
            }

            String messagePrefix = valueList.size() == 1 ? "allowed value is" : "allowed values are";

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messagePrefix + " " + valueList)
                    .addConstraintViolation();
        }

        return result;
    }
}