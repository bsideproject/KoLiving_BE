package com.koliving.api.validation;

import com.koliving.api.dto.SignUpDto;
import com.koliving.api.user.User;
import com.koliving.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailDuplicationValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpDto emailRequest = (SignUpDto) target;
        Optional<User> userOptional = userRepository.findByEmail(emailRequest.email());
        userOptional.ifPresent(user -> errors.rejectValue("email",  "duplication", emailRequest.email()));
    }
}
