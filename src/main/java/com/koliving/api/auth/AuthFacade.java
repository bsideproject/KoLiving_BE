package com.koliving.api.auth;

import static com.koliving.api.base.ServiceError.UNAUTHORIZED;

import com.koliving.api.auth.application.dto.TokenRequest;
import com.koliving.api.auth.application.dto.TokenResponse;
import com.koliving.api.auth.jwt.IJwtService;
import com.koliving.api.auth.jwt.JwtProvider;
import com.koliving.api.auth.jwt.JwtVo;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.dto.JwtTokenDto;
import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.exception.ConfirmationTokenException;
import com.koliving.api.token.blacklist.BlackAccessToken;
import com.koliving.api.token.blacklist.BlackListRepository;
import com.koliving.api.token.confirmation.ConfirmationToken;
import com.koliving.api.token.confirmation.ConfirmationTokenType;
import com.koliving.api.token.confirmation.IConfirmationTokenService;
import com.koliving.api.user.User;
import com.koliving.api.user.UserRepository;
import com.koliving.api.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthFacade {

    private final UserRepository userRepository;
    private final IConfirmationTokenService confirmationTokenService;
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;
    private final BlackListRepository blackListRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public void processEmailAuth(String email, ConfirmationTokenType tokenType) {
        ConfirmationToken newToken = confirmationTokenService.create(email, tokenType);
        ConfirmationToken savedToken = confirmationTokenService.save(newToken);
        eventPublisher.publishEvent(new ConfirmationTokenCreatedEvent(savedToken));
    }

    public void checkAuthMail(String token, String email) {
        try {
            confirmationTokenService.authenticate(token);
        } catch (RuntimeException e) {
            throw new ConfirmationTokenException(e.getMessage(), email, token);
        }
    }

    public boolean deleteConfirmationToken(String email) {
        return confirmationTokenService.delete(email) > 0;
    }

    @Transactional(readOnly = true)
    public JwtTokenDto issueAuthTokens(UserDetails userDetails) {
        String accessToken = issueAccessToken(userDetails);
        String refreshToken = jwtService.getRefreshToken(userDetails.getUsername());

        return JwtTokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public JwtTokenDto signUp(User user) {
        user.completeSignUp();
        userRepository.save(user);

        this.setAuthentication(user);

        String accessToken = issueAccessToken(user);
        String refreshToken = issueRefreshToken(user);
        jwtService.saveRefreshToken(user.getEmail(), refreshToken);

        return JwtTokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public void addToBlackList(String accessToken) {
        blackListRepository.save(parseBlackAccessToken(accessToken));
    }

    private String issueAccessToken(UserDetails userDetails) {
        JwtVo jwtVo = JwtVo.builder()
            .email(userDetails.getUsername())
            .roles(userDetails.getAuthorities())
            .build();

        return jwtProvider.generateAccessToken(jwtVo);
    }

    private String issueRefreshToken(UserDetails userDetails) {
        JwtVo jwtVo = JwtVo.builder()
            .email(userDetails.getUsername())
            .build();

        return jwtProvider.generateRefreshToken(jwtVo);
    }

    private void setAuthentication(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private BlackAccessToken parseBlackAccessToken(String accessToken) {
        return new BlackAccessToken(accessToken, jwtService.extractExpirationDate(accessToken));
    }

    public TokenResponse createToken(TokenRequest request) {
        final User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new KolivingServiceException(UNAUTHORIZED));

        user.checkPassword(passwordEncoder, request.password());

        final JwtTokenDto jwtTokenDto = issueAuthTokens(user);

        return TokenResponse.valueOf(jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());
    }
}
