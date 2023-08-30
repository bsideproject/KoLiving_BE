package com.koliving.api.auth.jwt;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class JwtVo {

    private String email;
    private Collection<? extends GrantedAuthority> roles;

    @Builder
    public JwtVo(String email, Collection<? extends GrantedAuthority> roles) {
        this.email = email;
        this.roles = roles;
    }

    public String joinRolesToString() {
        return this.roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
    }
}
