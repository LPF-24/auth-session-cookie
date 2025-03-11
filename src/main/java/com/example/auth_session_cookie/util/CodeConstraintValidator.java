package com.example.auth_session_cookie.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CodeConstraintValidator implements ConstraintValidator<ValidCode, String> {
    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        if (code == null || code.isBlank()) {
            return false;
        }

        try {
            int value = Integer.parseInt(code);
            return value >= 100000 && value <= 999999 && value % 4 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
