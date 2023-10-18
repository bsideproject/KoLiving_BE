package com.koliving.api.user;

import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.file.domain.ImageFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static com.koliving.api.base.ServiceError.UNAUTHORIZED;

@Entity(name = "TB_USER")
@DynamicInsert
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @Lob
    private String description;

    @OneToOne
    @JoinColumn(name = "IMAGE_FILE_ID")
    private ImageFile imageFile;

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

    private User(String email, String password, String firstName, String lastName, Gender gender, LocalDate birthDate, String description, ImageFile imageFile, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.description = description;
        this.imageFile = imageFile;
        this.userRole = userRole;
    }

    @Deprecated
    public static User valueOf(String email, String encodedPassword, UserRole role) {
        return new User(email, encodedPassword, null, null, null, null, null,null, role);
    }

    public static User of(ImageFile imageFile, Gender gender, String firstName, String lastName, LocalDate birthDate, String description) {
        return new User(null, null, firstName, lastName, gender, birthDate,description, imageFile, null);
    }
    public void setPassword(String password) {
        this.password = password;
        this.signUpStatus = SignUpStatus.PROFILE_INFORMATION_PENDING;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void completeSignUp() {
        this.bEnabled = true;
        this.signUpStatus = SignUpStatus.COMPLETED;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = userRole.getSecurityName();
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

    public void checkPassword(PasswordEncoder passwordEncoder, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, this.password)) {
            throw new KolivingServiceException(UNAUTHORIZED);
        }
    }

    public void update(User updatable) {
        this.imageFile = updatable.imageFile;
        this.firstName = updatable.firstName;
        this.lastName = updatable.lastName;
        this.gender = updatable.gender;
        this.birthDate = updatable.birthDate;
        this.description = updatable.description;
    }
}
