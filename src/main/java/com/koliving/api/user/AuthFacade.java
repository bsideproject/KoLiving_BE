package com.koliving.api.user;

import com.koliving.api.dto.TokenDto;
import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.exception.AuthException;
import com.koliving.api.provider.JwtProvider;
import com.koliving.api.token.IJwtService;
import com.koliving.api.token.blacklist.BlackAccessToken;
import com.koliving.api.token.blacklist.BlackListRepository;
import com.koliving.api.token.confirmation.ConfirmationToken;
import com.koliving.api.token.confirmation.IConfirmationTokenService;
import com.koliving.api.vo.JwtVo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthFacade {
    private final UserService userService;
    private final IConfirmationTokenService confirmationTokenService;
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;
    private final BlackListRepository blackListRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void processEmailAuth(String email) {
        ConfirmationToken newToken = confirmationTokenService.create(email);
        ConfirmationToken savedToken = confirmationTokenService.save(newToken);
        eventPublisher.publishEvent(new ConfirmationTokenCreatedEvent(savedToken));
    }

    public void checkAuthMail(String token, String email) {
        try {
            confirmationTokenService.authenticateToken(token);
        } catch (RuntimeException e) {
            throw new AuthException(e.getMessage(), email);
        }
    }

    public boolean deleteConfirmationToken(String email) {
        return confirmationTokenService.delete(email) > 0;
    }

    @Transactional(readOnly = true)
    public TokenDto login(String email, String password) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        this.setAuthenticationWithPassword(password, userDetails);

        String accessToken = issueAccessToken(userDetails);
        String refreshToken = jwtService.getRefreshToken(email);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDto signUp(User user) {
        user.completeSignUp();
        String accessToken = this.loginWithSignUp(user);

        userService.save(user);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    public void logout(String accessToken) {
        BlackAccessToken blackAccessToken = parseBlackAccessToken(accessToken);
        blackListRepository.save(blackAccessToken);
        SecurityContextHolder.clearContext();
    }

    private String loginWithSignUp(UserDetails userDetails) {
        this.setAuthentication(userDetails);

        return issueAccessToken(userDetails);
    }

    private void setAuthentication(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAuthenticationWithPassword(String password, UserDetails userDetails) {
        String storedPassword = userDetails.getPassword();
        if (!userService.isEqualPassword(password, storedPassword)) {
            throw new BadCredentialsException("Password not matched");
        };

        this.setAuthentication(userDetails);
    }

    private String issueAccessToken(UserDetails userDetails) {
        JwtVo jwtVo = JwtVo.builder()
                .email(userDetails.getUsername())
                .role(userDetails.getAuthorities().toString())
                .build();

        return jwtProvider.generateAccessToken(jwtVo);
    }

    private BlackAccessToken parseBlackAccessToken(String accessToken) {
        return BlackAccessToken.builder()
                .accessToken(accessToken)
                .expirationTime(jwtService.extractExpirationDate(accessToken))
                .build();
    }
}
