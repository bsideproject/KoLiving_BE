package com.koliving.api.auth;

import com.koliving.api.dto.AuthEmailRequestDto;
import com.koliving.api.dto.JwtTokenDto;
import com.koliving.api.dto.PasswordDto;
import com.koliving.api.dto.ProfileDto;
import com.koliving.api.dto.ResetPasswordDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.exception.DuplicateResourceException;
import com.koliving.api.exception.NonExistentResourceException;
import com.koliving.api.token.confirmation.ConfirmationTokenType;
import com.koliving.api.user.User;
import com.koliving.api.user.UserPropertyEditor;
import com.koliving.api.user.UserService;
import com.koliving.api.utils.HttpUtils;
import com.koliving.api.validation.EmailDuplicationValidator;
import com.koliving.api.validation.EmailPresenceValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 가입 API", description = "회원 가입 및 수정 컨트롤러")
@RestController
@RequestMapping("/api/${server.current-version}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final UserService userService;
    private final EmailDuplicationValidator emailDuplicationValidator;
    private final EmailPresenceValidator emailPresenceValidator;
    private final Provider<UserPropertyEditor> userPropertyEditorProvider;
    private final ModelMapper modelMapper;
    private final HttpUtils httpUtils;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(User.class, userPropertyEditorProvider.get());
    }

    @PostMapping("/sign-up")
    @Operation(
        summary = "sign-up email API", description = "회원가입 - 1. 이메일 입력 API",
        responses = {
            @ApiResponse(responseCode = "204", description = "인증 이메일 발송 성공", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이메일 입력값",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Invalid input of email", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"signUpDto\",\"field\":\"email\",\"code\":\"Email\",\"message\":\"not a well-formed email address\"}]}}"),
                        @ExampleObject(name = "Mismatched input of passwords", value = "{\"responseCode\": 302, \"error\": \"This email already exists : existent@koliving.com\"}")
                    }
                )
            ),
        }
    )
    public ResponseEntity sendAuthEmailForSignUp(final @Valid @RequestBody AuthEmailRequestDto authEmailRequestDto) {
        checkEmailDuplication(authEmailRequestDto, emailDuplicationValidator);

        authFacade.processEmailAuth(authEmailRequestDto.email(), ConfirmationTokenType.SIGN_UP);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/sign-up/confirm")
    @Operation(
        summary = "sign-up email confirmation API", description = "회원가입 - 1-1. 이메일 인증 API",
        parameters = {
            @Parameter(name = "token", description = "인증 토큰", example = "need to assign", required = true, content = @Content(schema = @Schema(type = "string"))),
            @Parameter(name = "email", description = "이메일 인증 신청자", example = "test@koliving.com", required = true, content = @Content(schema = @Schema(type = "string"))),
        },
        responses = {
            @ApiResponse(responseCode = "302", description = "이메일 인증 성공",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/{current-version}/auth/password"))},
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Success", value = "{\"responseCode\": 302, \"data\": \"email confirmation for sign-up success : test@koliving.com\"}"),}
                )
            ),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패 - 유효하지 않은 토큰",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/{current-version}/auth/login"))},
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Token Not Generated", value = "{\"responseCode\":400,\"error\":{\"errorMessage\" :\"The confirmation token is not a normal generated by the server\",\"email\":\"test@koliving.com\"}}"),
                        @ExampleObject(name = "Token Expired", value = "{\"responseCode\":400,\"error\":{\"errorMessage\" :\"The confirmation token has expired\",\"email\":\"test@koliving.com\"}}")
                    }
                )
            ),
            @ApiResponse(responseCode = "401", description = "이메일 인증 실패 - 이미 인증에 성공한 토큰 (예시에 따른 응답 헤더가 다름. description 참고)",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Email authentication completed", description = "(Location Header) : \"/api/{current-version}/auth/password\"", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}"),
                        @ExampleObject(name = "Setting up the password completed", description = "(Location Header) : \"/api/{current-version}/auth/profile\"", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}"),
                        @ExampleObject(name = "A registered user", description = "(Location Header) : \"/api/{current-version}/auth/login\"", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}")
                    }
                )
            )
        })
    public ResponseEntity<ResponseDto<String>> checkAuthEmailForSignUp(
            @RequestParam @Parameter(hidden = true) String token,
            @RequestParam @Parameter(hidden = true) String email,
            HttpServletRequest request) {

        authFacade.checkAuthMail(token, email);

        User newUser = User.builder()
                .email(email)
                .build();

        userService.save(newUser);

        return httpUtils.createResponseEntityWithRedirect(
                httpUtils.createSuccessResponse("email confirmation success for sign-up : " + email, HttpStatus.FOUND.value()),
                httpUtils.getRedirectUri(request, "/password")
        );
    }

    @PostMapping("/password")
    @Operation(
        summary = "sign-up password API", description = "회원가입 - 2. 비밀번호 입력 API",
        parameters = {
            @Parameter(name = "email", required = true, description = "User's email", example = "koliving@gmail.com")
        },
        responses = {
            @ApiResponse(responseCode = "201", description = "비밀번호 설정 성공", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 비밀번호 입력값",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Invalid input of password", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"passwordDto\",\"field\":\"password\",\"code\":\"Size\",\"message\":\"size must be between 8 and 20\"}]}}"),}
                )
            ),
    })
    public ResponseEntity setPassword(final @Valid @RequestBody PasswordDto passwordDto, @RequestParam("email") @Parameter(hidden = true) User user) {
        userService.setPassword(user, passwordDto.password());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/profile")
    @Operation(
        summary = "sign-up profile API", description = "회원가입 - 3. 개인정보 입력 API",
        parameters = {
            @Parameter(name = "email", required = true, description = "User's email", example = "koliving@gmail.com")
        },
        responses = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Success", value = "{\"responseCode\": 201, \"data\": \"sign-up complete\"}"),}
                )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 프로필 입력값",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Invalid input of profile", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"profileDto\",\"field\":\"firstName\",\"code\":\"NotBlank\",\"message\":\"may not be empty\"}]}}"),}
                )
            )
        })
    public ResponseEntity setProfile(final @Valid @RequestBody ProfileDto profileDto, @RequestParam("email") @Parameter(hidden = true) User user) {
        modelMapper.map(profileDto, user);
        JwtTokenDto authToken = authFacade.signUp(user);
        authFacade.deleteConfirmationToken(user.getEmail());

        return httpUtils.createResponseEntityWithCookies(
                httpUtils.createSuccessResponse("sign-up complete", HttpStatus.CREATED.value()),
                httpUtils.getResponseCookieOfAccessToken(authToken.getAccessToken()),
                httpUtils.getResponseCookieOfRefreshToken(authToken.getRefreshToken())
        );
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = "reset password - send auth-email API", description = "회원 수정 (비밀번호 재설정) - 1. 인증 이메일 발송 API",
        responses = {
            @ApiResponse(responseCode = "204", description = "인증 이메일 발송 성공", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이메일 입력값",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Invalid input of email", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"resetPasswordDto\",\"field\":\"email\",\"code\":\"Email\",\"message\":\"not a well-formed email address\"}]}}"),
                        @ExampleObject(name = "Non existent email", value = "{\"responseCode\":400, \"error\": \"This email doesn't exist : nonexistent@koliving.com\"}"),
                    }
                )
            ),
        }
    )
    public ResponseEntity sendAuthEmailForResetPassword(final @Valid @RequestBody AuthEmailRequestDto authEmailRequestDto) {
        checkEmailPresence(authEmailRequestDto, emailPresenceValidator);

        authFacade.processEmailAuth(authEmailRequestDto.email(), ConfirmationTokenType.RESET_PASSWORD);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/reset-password/confirm")
    @Operation(
        summary = "reset password - confirmation API", description = "회원 수정 (비밀번호 재설정) - 2. 이메일 인증 API",
        parameters = {
            @Parameter(name = "token", description = "인증 토큰", example = "need to assign", required = true, content = @Content(schema = @Schema(type = "string"))),
            @Parameter(name = "email", description = "이메일 인증 신청자", example = "test@koliving.com", required = true, content = @Content(schema = @Schema(type = "string"))),
        },
        responses = {
            @ApiResponse(responseCode = "302", description = "이메일 인증 성공",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/{current-version}/auth/reset-password"))},
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Success", value = "{\"responseCode\": 302, \"data\": \"email confirmation success for reset-password : test@koliving.com\"}"),}
                )
            ),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패 - 유효하지 않은 토큰",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/{current-version}/auth/login"))},
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Token Not Generated", value = "{\"responseCode\":400,\"error\":{\"errorMessage\" :\"The confirmation token is not a normal generated by the server\",\"email\":\"test@koliving.com\"}}"),
                        @ExampleObject(name = "Token Expired", value = "{\"responseCode\":400,\"error\":{\"errorMessage\" :\"The confirmation token has expired\",\"email\":\"test@koliving.com\"}}")
                    }
                )
            ),
            @ApiResponse(responseCode = "401", description = "이메일 인증 실패 - 이미 인증에 성공한 토큰",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/{current-version}/auth/reset-password"))},
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Email authentication completed", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}"),
                    }
                )
            )
        }
    )
    public ResponseEntity<ResponseDto<String>> checkAuthEmailForResetPassword(
            @RequestParam @Parameter(hidden = true) String token,
            @RequestParam @Parameter(hidden = true) String email,
            HttpServletRequest request) {

        authFacade.checkAuthMail(token, email);

        return httpUtils.createResponseEntityWithRedirect(
                httpUtils.createSuccessResponse("email confirmation success for reset-password : " + email, HttpStatus.FOUND.value()),
                httpUtils.getRedirectUri(request, "/reset-password")
        );
    }

    @PutMapping("/reset-password")
    @Operation(
        summary = "reset password API", description = "회원 수정 (비밀번호 재설정) - 3. 비밀번호 재설정 API",
        parameters = {
            @Parameter(name = "email", required = true, description = "User's email", example = "koliving@gmail.com")
        },
        responses = {
            @ApiResponse(responseCode = "201", description = "비밀번호 설정 성공", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 비밀번호 입력값",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                        @ExampleObject(name = "Invalid input of password", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"resetPasswordDto\",\"field\":\"password\",\"code\":\"Size\",\"message\":\"size must be between 8 and 20\"}]}}"),
                        @ExampleObject(name = "Mismatched input of passwords", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"resetPasswordDto\",\"field\":\"passwordVerify\",\"code\":\"PasswordMatches\",\"message\":\"The Password and verification password does not match\"}]}}"),
                    }
                )
            ),
        }
    )
    public ResponseEntity resetPassword(final @Valid @RequestBody ResetPasswordDto resetPasswordDto, @RequestParam("email") @Parameter(hidden = true) User user) {
        userService.setPassword(user, resetPasswordDto.password());
        authFacade.deleteConfirmationToken(user.getEmail());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private void checkEmailDuplication(AuthEmailRequestDto authEmailRequestDto, Validator validator) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(authEmailRequestDto, "authEmailRequestDto");
        validator.validate(authEmailRequestDto, errors);
        if (errors.hasErrors()) {
            String errorField = errors.getFieldError().getField();
            String errorCode = errors.getFieldError().getCode();
            String duplicatedEmail = errors.getFieldError().getDefaultMessage();
            throw new DuplicateResourceException(errorField + "_" + errorCode + ":" + duplicatedEmail);
        }
    }

    private void checkEmailPresence(AuthEmailRequestDto authEmailRequestDto, Validator validator) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(authEmailRequestDto, "authEmailRequestDto");
        validator.validate(authEmailRequestDto, errors);
        if (errors.hasErrors()) {
            String errorField = errors.getFieldError().getField();
            String errorCode = errors.getFieldError().getCode();
            String nonExistsEmail = errors.getFieldError().getDefaultMessage();
            throw new NonExistentResourceException(errorField + "_" + errorCode + ":" + nonExistsEmail);
        }
    }
}
