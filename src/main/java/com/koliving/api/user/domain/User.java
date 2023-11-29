package com.koliving.api.user.domain;

import static com.koliving.api.base.ServiceError.UNAUTHORIZED;

import com.koliving.api.base.domain.BaseEntity;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.file.domain.ImageFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity(name = "TB_USER")
@DynamicInsert
@DynamicUpdate
@Getter
@ToString(of = "id")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

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

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Notification> receivedNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Notification> sentNotifications = new ArrayList<>();

    @Builder
    public User(String email) {
        this.email = email;
        this.signUpStatus = SignUpStatus.PASSWORD_VERIFICATION_PENDING;
        this.userRole = UserRole.USER;
    }

    private User(String email, String password, String firstName, String lastName, Gender gender, LocalDate birthDate,
        String description, ImageFile imageFile, UserRole userRole) {
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
        return new User(email, encodedPassword, RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(4), Gender.MALE, LocalDate.now(), RandomStringUtils.randomAlphabetic(200), null, role);
    }

    public static User of(ImageFile imageFile, Gender gender, String firstName, String lastName, LocalDate birthDate,
        String description) {
        return new User(null, null, firstName, lastName, gender, birthDate, description, imageFile, null);
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

    public void addReceivedNotification(Notification notification) {
        this.receivedNotifications.add(notification);
    }

    public void addSentNotification(Notification notification) {
        this.sentNotifications.add(notification);
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

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getImageProfile() {
        return Objects.isNull(this.getImageFile()) ? null : this.imageFile.getPath();
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
