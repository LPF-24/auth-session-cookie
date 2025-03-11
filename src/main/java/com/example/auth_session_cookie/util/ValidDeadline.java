package com.example.auth_session_cookie.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DeadlineConstraintValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDeadline {
    String message() default "The deadline must be today or in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
