package com.koliving.api.user;

import com.koliving.api.dto.SignUpDto;
import com.koliving.api.exception.DuplicateResourceException;
import com.koliving.api.validation.EmailDuplicationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${server.current-version}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailDuplicationValidator emailDuplicationValidator;
    private final AuthFacade authFacade;

    @PostMapping("/sign-up")
    public ResponseEntity signUp(final @Valid @RequestBody SignUpDto signUpDto) {
        BindException error = new BindException(signUpDto, "signUpDto");
        emailDuplicationValidator.validate(signUpDto, error);
        if (error.hasErrors()) {
            String errorField = error.getFieldError().getField();
            String errorCode = error.getFieldError().getCode();
            String duplicatedEmail = error.getFieldError().getDefaultMessage();
            throw new DuplicateResourceException(errorField + "_" + errorCode + ":" + duplicatedEmail);
        }

        authFacade.processEmailAuth(signUpDto.email());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
