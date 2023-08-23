package com.koliving.api.validation;

import com.koliving.api.annotation.PasswordMatches;
import com.koliving.api.dto.ResetPasswordDto;
import com.koliving.api.i18n.MessageSource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ResetPasswordDto> {

    private final MessageSource messageSource;

    @Override
    public boolean isValid(ResetPasswordDto value, ConstraintValidatorContext context) {
        String password = value.password();
        String passwordVerify = value.passwordVerify();

        if (!password.equals(passwordVerify)) {
//            String messagePattern = messageSource.getMessage("mismatched_password", null, LocaleContextHolder.getLocale());
            String messagePattern = "mismatched_password";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messagePattern)
                    .addPropertyNode("passwordVerify")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
