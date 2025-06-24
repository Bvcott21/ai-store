package com.bucott.store.validation;

import com.bucott.store.dto.auth.RegisterRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequestDTO> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RegisterRequestDTO requestDTO, ConstraintValidatorContext context) {
        if (requestDTO == null) {
            return true; // Let other validators handle null objects
        }

        String password = requestDTO.getPassword();
        String confirmPassword = requestDTO.getConfirmPassword();

        if (password == null && confirmPassword == null) {
            return true; // Both null is valid for this validator
        }

        if (password == null || confirmPassword == null) {
            return false; // One null, one not null
        }

        return password.equals(confirmPassword);
    }
}