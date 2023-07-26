package com.koliving.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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

}
