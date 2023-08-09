package com.koliving.api.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koliving.api.dto.LoginDto;
import com.koliving.api.exception.LoginInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, Validator validator) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LoginDto loginDto = parseLoginDto(request);
        validateLoginDtoWithSpring(loginDto);

        return authenticate(request, loginDto);
    }

    private LoginDto parseLoginDto(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateLoginDtoWithSpring(LoginDto loginDto) throws LoginInvalidException {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(loginDto, "loginDto");
        validator.validate(loginDto, errors);
        if (errors.hasErrors()) {
            throw new LoginInvalidException(errors);
        }
    }

    private Authentication authenticate(HttpServletRequest request, LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
