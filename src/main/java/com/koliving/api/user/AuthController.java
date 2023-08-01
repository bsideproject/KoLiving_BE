package com.koliving.api.user;

import com.koliving.api.dto.PasswordDto;
import com.koliving.api.dto.ProfileDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.SignUpDto;
import com.koliving.api.dto.TokenDto;
import com.koliving.api.exception.DuplicateResourceException;
import com.koliving.api.validation.EmailDuplicationValidator;
import jakarta.inject.Provider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${server.current-version}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final UserService userService;
    private final EmailDuplicationValidator emailDuplicationValidator;
    private final Provider<UserPropertyEditor> userPropertyEditorProvider;
    private final ModelMapper modelMapper;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        dataBinder.registerCustomEditor(User.class, userPropertyEditorProvider.get());
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUp(final @Valid @RequestBody SignUpDto signUpDto) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(signUpDto, "signUpDto");
        emailDuplicationValidator.validate(signUpDto, errors);
        if (errors.hasErrors()) {
            String errorField = errors.getFieldError().getField();
            String errorCode = errors.getFieldError().getCode();
            String duplicatedEmail = errors.getFieldError().getDefaultMessage();
            throw new DuplicateResourceException(errorField + "_" + errorCode + ":" + duplicatedEmail);
        }

        authFacade.processEmailAuth(signUpDto.email());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/password")
    public ResponseEntity setPassword(final @Valid @RequestBody PasswordDto passwordDto, @RequestParam("email") User user) {
        userService.setPassword(user, passwordDto.password());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/profile")
    public ResponseEntity<ResponseDto<TokenDto>> setProfile(final @Valid @RequestBody ProfileDto profileDto, @RequestParam("email") User user) {
        modelMapper.map(profileDto, user);
        TokenDto accessToken = authFacade.signUp(user);

        return createSuccessResponse(accessToken, HttpStatus.CREATED);
    }

    private <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(T data, HttpStatus status) {
        ResponseDto<T> response = ResponseDto.success(data, status.value());

        return new ResponseEntity<>(response, status);
    }
}
