package com.koliving.api.auth;

import com.koliving.api.dto.JwtTokenDto;
import com.koliving.api.dto.PasswordDto;
import com.koliving.api.dto.ProfileDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.SignUpDto;
import com.koliving.api.exception.DuplicateResourceException;
import com.koliving.api.user.User;
import com.koliving.api.user.UserPropertyEditor;
import com.koliving.api.user.UserService;
import com.koliving.api.validation.EmailDuplicationValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Provider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "회원 가입 API", description = "회원 가입 및 수정 컨트롤러")
@RestController
@RequestMapping("/api/${server.current-version}/auth")
public class AuthController {

    private final AuthFacade authFacade;
    private final UserService userService;
    private final EmailDuplicationValidator emailDuplicationValidator;
    private final Provider<UserPropertyEditor> userPropertyEditorProvider;
    private final ModelMapper modelMapper;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public AuthController(AuthFacade authFacade,
                          UserService userService,
                          EmailDuplicationValidator emailDuplicationValidator,
                          Provider<UserPropertyEditor> userPropertyEditorProvider,
                          ModelMapper modelMapper,
                          @Value("${jwt.expiration:2}") int accessTokenValidity,
                          @Value("${jwt.refreshExpiration:30}") int refreshTokenValidity) {
        this.authFacade = authFacade;
        this.userService = userService;
        this.emailDuplicationValidator = emailDuplicationValidator;
        this.userPropertyEditorProvider = userPropertyEditorProvider;
        this.modelMapper = modelMapper;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        dataBinder.registerCustomEditor(User.class, userPropertyEditorProvider.get());
    }

    @PostMapping("/sign-up")
    @Operation(
        summary = "sign-up email API", description = "회원가입 - 1. 이메일 입력 API",
        responses = {
            @ApiResponse(responseCode = "201", description = "인증 이메일 발송 성공", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이메일 입력값",
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Invalid input of email", value = "{\"responseCode\":400,\"error\":{\"errors\":[{\"objectName\":\"signUpDto\",\"field\":\"email\",\"code\":\"Email\",\"message\":\"not a well-formed email address\"}]}}"),}
                )
            ),
        }
    )
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

    @GetMapping("/sign-up/confirm")
    @Operation(
        summary = "sign-up email confirmation API",
        description = "회원가입 - 1-1. 이메일 인증 API",
        parameters = {
            @Parameter(name = "token", description = "인증 토큰", example = "need to assign", required = true, content = @Content(schema = @Schema(type = "string"))),
            @Parameter(name = "email", description = "이메일 인증 신청자", example = "test@koliving.com", required = true, content = @Content(schema = @Schema(type = "string"))),
        },
        responses = {
            @ApiResponse(responseCode = "302", description = "이메일 인증 성공",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/auth/{current-version}/password"))},
                content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {@ExampleObject(name = "Success", value = "{\"responseCode\": 302, \"data\": \"sign-up complete\"}"),}
                )
            ),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패 - 유효하지 않은 토큰",
                headers = {@Header(name = "Location", schema = @Schema(type = "string", example = "/api/login"))},
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
                        @ExampleObject(name = "Email authentication completed", description = "(Location Header) : \"/api/auth/{current-version}/password\"", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}"),
                        @ExampleObject(name = "Setting up the password completed", description = "(Location Header) : \"/api/auth/{current-version}/profile\"", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}"),
                        @ExampleObject(name = "A registered user", description = "(Location Header) : \"/api/login\"", value = "{\"responseCode\":401,\"error\":{\"errorMessage\" :\"The confirmation token already confirmed\",\"email\":\"test@koliving.com\"}}")
                    }
                )
            )
        })
    public ResponseEntity<ResponseDto<String>> checkAuthEmail(
            @RequestParam @Parameter(hidden = true) String token,
            @RequestParam @Parameter(hidden = true) String email,
            HttpServletRequest request) {

        authFacade.checkAuthMail(token, email);   
        authFacade.deleteConfirmationToken(token);

        User newUser = User.builder()
                .email(email)
                .build();

        userService.save(newUser);

        return createRedirectResponse(ResponseDto.success(email, HttpStatus.FOUND.value()), getRedirectUri(request, "/password"));
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
        summary = "sign-up profile API",
        description = "회원가입 - 3. 개인정보 입력 API",
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
    public ResponseEntity setProfile(final @Valid @RequestBody ProfileDto profileDto, @RequestParam("email") @Parameter(hidden = true) User user, HttpServletResponse response) {
        modelMapper.map(profileDto, user);
        JwtTokenDto authToken = authFacade.signUp(user);

        setResponse(response, getCookieOfAccessToken(authToken.getAccessToken()), getCookieOfRefreshToken(authToken.getAccessToken()));

        return createSuccessResponse(ResponseDto.success("sign-up complete", HttpStatus.CREATED.value()));
    }

    private ResponseEntity createSuccessResponse(ResponseDto body) {
        return new ResponseEntity<>(body, null, body.getResponseCode());
    }

    private ResponseEntity createRedirectResponse(ResponseDto body, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUri);

        return new ResponseEntity<>(body, headers, body.getResponseCode());
    }

    private String getRedirectUri(HttpServletRequest request, String redirectPath) {
        URI currentUri = URI.create(request.getRequestURI());

        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toUri().toString();
    }

    private void setResponse(HttpServletResponse response, Cookie accessToken, Cookie refreshToken) {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(accessToken);
        response.addCookie(refreshToken);
    }

    private Cookie getCookieOfAccessToken(String accessTokenValue) {
        Cookie accessToken = new Cookie("access_token", accessTokenValue);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setMaxAge((int) (60 * 60 * accessTokenValidity));

        return accessToken;
    }

    private Cookie getCookieOfRefreshToken(String refreshTokenValue) {
        Cookie refreshToken = new Cookie("refresh_token", refreshTokenValue);
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        refreshToken.setMaxAge((int) (60 * 60 * 24 * refreshTokenValidity));

        return refreshToken;
    }
}
