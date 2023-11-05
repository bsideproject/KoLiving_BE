package com.koliving.api.user;

import com.koliving.api.user.application.UserService;
import com.koliving.api.user.application.dto.UserResponse;
import com.koliving.api.user.domain.User;
import com.koliving.api.user.infra.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.koliving.api.user.UserUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @DisplayName("save() 성공 : 신규 회원 DB 저장")
    void save_success() {
        String dummyEmail = "test@koliving.com";
        String dummyPassword = "KolivingPwd!@";
        User dummyUser = createUser(dummyEmail, dummyPassword);

        when(userRepository.save(any(User.class))).thenReturn(dummyUser);
        User savedUser = userService.save(dummyUser);

        verify(userRepository, times(1)).save(dummyUser);

        assertEquals(savedUser.getEmail(), dummyEmail);
        assertEquals(savedUser.getPassword(), dummyPassword);
    }

    @Test
    @DisplayName("setPassword() 성공 : 회원 비밀번호 설정")
    void setPassword_success() {
        String dummyEmail = "test@koliving.com";
        String dummyPassword = "KolivingPwd!@";
        User dummyUser = createUser(dummyEmail, dummyPassword);

        String encodedPassword = "encoded_password";

        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        userService.setPassword(dummyUser, dummyPassword);

        verify(passwordEncoder, times(1)).encode(dummyPassword);

        assertEquals(dummyUser.getPassword(), encodedPassword);
    }

    @Test
    @DisplayName("isEqualPassword() 성공 : 저장된 암호 패스워드의 원시 패스워드 여부 확인")
    void isEqualPassword_success() {
        String dummyPassword = "KolivingPwd!@";
        String savedPassword = "encoded password";

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertTrue(userService.isEqualPassword(dummyPassword, savedPassword));
    }

    @Test
    @DisplayName("isEqualPassword() 실패 : 저장된 암호 패스워드의 원시 패스워드 여부 확인")
    void isEqualPassword_failure_mismatched_password() {
        String mismatchedPassword = "wrong password";
        String savedPassword = "encoded password";

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertFalse(userService.isEqualPassword(mismatchedPassword, savedPassword));
    }

    @Test
    @DisplayName("list() 성공 : 회원 목록을 UserResponse 매핑 및 리스트 리턴")
    void list_success() {
        String dummyEmail = "test@koliving.com";
        User dummyUser = createUser(dummyEmail);

        String dummyEmail2 = "test2@koliving.com";
        User dummyUser2 = createUser(dummyEmail2);

        String dummyEmail3 = "test3@koliving.com";
        User dummyUser3 = createUser(dummyEmail3);

        List<User> userList = Arrays.asList(dummyUser, dummyUser2, dummyUser3);

        when(userRepository.findAll()).thenReturn(userList);
        List<UserResponse> actual = userService.list();

        verify(userRepository, times(1)).findAll();

        List<UserResponse> expected = userList.stream()
                .map(UserResponse::valueOf)
                .toList();

        assertTrue(actual.equals(expected));
        assertTrue(actual.containsAll(expected));
    }
}
