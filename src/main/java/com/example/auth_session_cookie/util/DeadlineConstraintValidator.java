package com.example.auth_session_cookie.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DeadlineConstraintValidator implements ConstraintValidator<ValidDeadline, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime deadLine, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("ConstraintValidator isValid started");
        if (deadLine == null) {
            return true;
        }
        return !deadLine.isBefore(LocalDateTime.now().withSecond(0).withNano(0));
    }
}
