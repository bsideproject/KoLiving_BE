package com.koliving.api.user;

import com.koliving.api.dto.SignUpDto;
import com.koliving.api.exception.DuplicateResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${server.current-version}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Validator emailDuplicationValidator;
    private final AuthFacade authFacade;

    @InitBinder("SignUpDto")
    public void initBinderForEvent(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(emailDuplicationValidator);
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUp(final @Valid @RequestBody SignUpDto signUpDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorField = bindingResult.getFieldError().getField();
            String errorCode = bindingResult.getFieldError().getCode();
            String duplicatedEmail = bindingResult.getFieldError().getDefaultMessage();
            throw new DuplicateResourceException(errorField + "_" + errorCode + ":" + duplicatedEmail);
        }

        authFacade.processEmailAuth(signUpDto.email());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
