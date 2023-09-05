package com.koliving.api.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.koliving.api.user.UserUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("loadUserByUsername() 성공 : 기존 회원 DB 조회")
    void loadUserByUsername_success() {
        String dummyEmail = "test@koliving.com";
        String dummyPassword = "KolivingPwd!@";
        User dummyUser = createUser(dummyEmail, dummyPassword);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));

        UserDetails savedUser = userService.loadUserByUsername(dummyEmail);

        verify(userRepository, times(1)).findByEmail(anyString());

        assertEquals(dummyUser.getEmail(), savedUser.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername() 실패 : 존재하지 않는 회원 DB 조회")
    void loadUserByUsername_failure_non_exists_email() {
        String nonExistsEmail = "noAtMark";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
           userService.loadUserByUsername(nonExistsEmail);
        });

        verify(userRepository, times(1)).findByEmail(anyString());
    }
}
