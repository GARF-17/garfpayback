package com.garf.garfpay.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, CharSequence> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValidEnum annotation) {
        // Extraemos todos los valores posibles del Enum
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        // Si es nulo, dejamos que @NotNull haga su trabajo. Aquí solo validamos el contenido.
        if (value == null) {
            return true;
        }
        return acceptedValues.contains(value.toString());
    }
}