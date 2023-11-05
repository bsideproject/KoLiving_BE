package com.koliving.api.validation;

import com.koliving.api.dto.AuthEmailRequestDto;
import com.koliving.api.user.domain.User;
import com.koliving.api.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailPresenceValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return AuthEmailRequestDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuthEmailRequestDto emailRequest = (AuthEmailRequestDto) target;
        Optional<User> userOptional = userRepository.findByEmail(emailRequest.email());
        if (userOptional.isEmpty()) {
            errors.rejectValue("email",  "not_exists", emailRequest.email());
        }
    }
}
