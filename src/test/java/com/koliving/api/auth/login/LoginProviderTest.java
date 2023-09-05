package com.koliving.api.auth.login;

import com.koliving.api.user.User;
import com.koliving.api.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static com.koliving.api.user.UserUtils.createAuthentication;
import static com.koliving.api.user.UserUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginProviderTest {

    @Mock
    UserService userService;

    @InjectMocks
    LoginProvider loginProvider;

    @Test
    @DisplayName("authenticate() 성공 : 로그인 인증")
    void authenticate_success() {
        String dummyEmail = "test@koliving.com";
        String dummyPassword = "KolivingPwd12";
        User dummyUser = createUser(dummyEmail, dummyPassword);

        when(userService.loadUserByUsername(dummyEmail)).thenReturn(dummyUser);
        when(userService.isEqualPassword(dummyPassword, dummyUser.getPassword())).thenReturn(true);

        Authentication auth = createAuthentication(dummyEmail, dummyPassword);
        Authentication authentication = loginProvider.authenticate(auth);

        assertNotNull(authentication);
        assertEquals(dummyEmail, authentication.getName());
    }
}
