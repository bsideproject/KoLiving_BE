package com.koliving.api.user;

import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.token.confirmation.ConfirmationToken;
import com.koliving.api.token.confirmation.IConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Transactional
@Service
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final IConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    @Override
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
    public void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    @Override
    public void completeSignUp(User user) {
        user.completeSignUp();
        User newUser = userRepository.save(user);

        // TODO : Login() 생성 필요
//        this.login(newUser);
    }
}
