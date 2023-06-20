package com.koliving.api.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Integer id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String description;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private SignUpStatus signUpStatus;

    private boolean bEnabled;
    private boolean bLocked;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    @Builder
    public User(String email) {
        this.email = email;
        this.signUpStatus = SignUpStatus.PASSWORD_VERIFICATION_PENDING;
        this.userRole = UserRole.USER;
        this.bEnabled = false;
        this.bLocked = false;
    }

    public void setPassword(String password) {
        this.password = password;
        this.signUpStatus = SignUpStatus.PROFILE_INFORMATION_PENDING;
    }

    public void completeSignUp() {
        this.bEnabled = true;
        this.signUpStatus = SignUpStatus.COMPLETED;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = userRole.name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return bEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bLocked;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO : need field related to user expiration
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO : need field related to password expiration
        return true;
    }
}
