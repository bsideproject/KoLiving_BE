package com.koliving.api.user;

import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.token.ConfirmationToken;
import com.koliving.api.token.IConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final IConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    @Override
    @Transactional
    public void saveTokenAndSendEmail(String email) {
        ConfirmationToken newToken = confirmationTokenService.createToken(email);
        ConfirmationToken savedToken = confirmationTokenService.saveToken(newToken);
        eventPublisher.publishEvent(new ConfirmationTokenCreatedEvent(savedToken));
    }

    @Override
    public User signUp(String email) {
        User newUser = User.builder()
                .email(email)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }
}
