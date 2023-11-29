package com.koliving.api.user.application;

import static com.koliving.api.user.domain.NotifyType.RECEIVE;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.file.infra.ImageFileRepository;
import com.koliving.api.my.application.dto.UserProfileUpdateRequest;
import com.koliving.api.user.application.dto.NotificationResponse;
import com.koliving.api.user.application.dto.UserResponse;
import com.koliving.api.user.domain.Notification;
import com.koliving.api.user.domain.NotifyType;
import com.koliving.api.user.domain.User;
import com.koliving.api.user.infra.NotificationRepository;
import com.koliving.api.user.infra.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ImageFileRepository imageFileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    @Override
    public boolean isEqualPassword(String rawPassword, String hashPassword) {
        return passwordEncoder.matches(rawPassword, hashPassword);
    }

    @Override
    public List<UserResponse> list() {
        return userRepository.findAll()
            .stream()
            .map(UserResponse::valueOf)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateProfile(UserProfileUpdateRequest request, Long userId) {
        ImageFile imageFile = getImageFile(request);
        User updatable = request.toUser(imageFile);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new KolivingServiceException(ServiceError.RECORD_NOT_EXIST));
        user.update(updatable);
    }

    private ImageFile getImageFile(UserProfileUpdateRequest request) {
        return imageFileRepository.findById(request.profileId())
            .orElseThrow(() -> new KolivingServiceException(ServiceError.RECORD_NOT_EXIST));
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new KolivingServiceException(ServiceError.RECORD_NOT_EXIST));
        return UserResponse.valueOf(user);
    }

    public List<NotificationResponse> getNotifications(NotifyType notifyType, User user) {
        if (notifyType.isSend()) {
            return notificationRepository.findBySenderId(user.getId(), getOneMonthBefore())
                .stream()
                .map(notification -> NotificationResponse.of(notifyType, notification))
                .collect(Collectors.toList());
        }

        if (notifyType.isReceive()) {
            return notificationRepository.findByReceiverId(user.getId(), getOneMonthBefore())
                .stream()
                .map(notification -> NotificationResponse.of(notifyType, notification))
                .collect(Collectors.toList());
        }

        final List<Notification> receives = notificationRepository.findByReceiverId(user.getId(), getOneMonthBefore());
        final List<Notification> sent = notificationRepository.findBySenderId(user.getId(), getOneMonthBefore());

        return NotificationResponse.ofList(receives, sent);
    }

    @Transactional
    public void notificationConfirmed(Long id, User user) {
        final Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new KolivingServiceException(ServiceError.RECORD_NOT_EXIST));
        notification.confirm(user);
    }

    public List<NotificationResponse> hasCheckedNewNotification(User user) {
        return notificationRepository.findNotConfirmedByReceiverId(user.getId(), getOneMonthBefore())
            .stream()
            .map(notification -> NotificationResponse.of(RECEIVE, notification))
            .collect(Collectors.toList());
    }

    public LocalDateTime getOneMonthBefore() {
        return LocalDateTime.now().minusMonths(1);
    }
}
