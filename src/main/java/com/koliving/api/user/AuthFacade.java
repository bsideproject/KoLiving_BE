package com.koliving.api.user;

import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.provider.JwtProvider;
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
    private final JwtProvider jwtProvider;
    private final IConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public void processEmailAuth(String email) {
        ConfirmationToken newToken = confirmationTokenService.createToken(email);
        ConfirmationToken savedToken = confirmationTokenService.saveToken(newToken);
        eventPublisher.publishEvent(new ConfirmationTokenCreatedEvent(savedToken));
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        UserDetails userDetails = userService.loadUserByUsername(email);;
        this.setAuthenticationWithPassword(password, userDetails);

        return issueAccessToken(userDetails);
    }

    public String signUp(String email) {
        User user = (User) userService.loadUserByUsername(email);
        user.completeSignUp();

        String accessToken = this.loginWithSignUp(user);

        return accessToken;
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
}
