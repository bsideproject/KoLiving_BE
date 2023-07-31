package com.koliving.api.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity(name = "USER")
@Getter @ToString @EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Integer id;
    private String email;
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "SIGNUP_STATUS")
    private SignUpStatus signUpStatus;

    @Column(name = "B_ENABLED")
    private boolean bEnabled;
    @Column(name = "B_LOCKED")
    private boolean bLocked;

    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate;

    @Builder
    public User(String email) {
        this.email = email;
        this.signUpStatus = SignUpStatus.PASSWORD_VERIFICATION_PENDING;
        this.userRole = UserRole.USER;
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
