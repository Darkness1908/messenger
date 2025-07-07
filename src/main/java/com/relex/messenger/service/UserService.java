package com.relex.messenger.service;

import com.relex.messenger.dto.*;
import com.relex.messenger.entity.*;
import com.relex.messenger.enums.NotificationType;
import com.relex.messenger.enums.UserStatus;
import com.relex.messenger.repository.NotificationRepository;
import com.relex.messenger.repository.UserRepository;
import com.relex.messenger.repository.UserUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserUserRepository userUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;
    private final JwtService jwtService;

    @Transactional
    public void deleteAccount(@NotNull User user, String token) {
        if (user.getDeletedAt() != null) {
            return;
        }

        user.setDeletedAt(LocalDateTime.now());
        jwtService.blacklistToken(token);
        userRepository.save(user);
    }

    @Transactional
    public void blockUser(Long blockingUserId, User user) {
        if (!userRepository.existsById(blockingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (user.getId().equals(blockingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot block yourself");
        }

        if (isBlocked(user.getId(), blockingUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user is already banned");
        }

        notificationRepository.deleteAllByNotifiedIdAndSenderIdAndTypeNot(blockingUserId,
                user.getId(), NotificationType.MESSAGE);

        if (userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), blockingUserId, UserStatus.FRIEND)) {
            UserUser userUser = userUserRepository.findByFirstUserIdAndSecondUserId(user.getId(), blockingUserId);
            userUser.setUserStatus(UserStatus.BLOCKED);
            return;
        }

        if (userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                blockingUserId, user.getId(), UserStatus.FRIEND)) {
            UserUser userUser = userUserRepository.findByFirstUserIdAndSecondUserId(
                    blockingUserId, user.getId());
            userUserRepository.delete(userUser);
        }

        User banningUser = userRepository.getReferenceById(blockingUserId);
        UserUser userUser = new UserUser(banningUser, user, UserStatus.BLOCKED);
        userUserRepository.save(userUser);
    }

    public void unblockUser(Long unblockingUserId, User user) {
        if (!userRepository.existsById(unblockingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (user.getId().equals(unblockingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot unblock yourself");
        }

        if (!isBlocked(user.getId(), unblockingUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user is not blocked");
        }

        UserUser userUser = userUserRepository.findByFirstUserIdAndSecondUserId(
                user.getId(), unblockingUserId);
        userUserRepository.delete(userUser);
    }

    public void inviteToFriends(Long invitingUserId, User inviter) {
        if (!userRepository.existsById(invitingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (inviter.getId().equals(invitingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot invite yourself");
        }

        if (isFriend(invitingUserId, inviter.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User is already a friend");
        }

        if (isInvited(invitingUserId, inviter.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Either you or the other user is invited for each other");
        }

        if (isEitherUserBlocked(invitingUserId, inviter.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Either you or the other user is blocked for each other");
        }

        User invitingUser = userRepository.getReferenceById(invitingUserId);
        Notification notification = new Notification(invitingUser, inviter, NotificationType.FRIENDSHIP);
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteFriend(Long friendId, User user) {
        if (!userRepository.existsById(friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (user.getId().equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot delete yourself from friends");
        }

        if (!isFriend(friendId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user is not your friend");
        }

        notificationRepository.deleteAllByNotifiedIdAndSenderIdAndTypeNot(user.getId(),
                friendId, NotificationType.MESSAGE);


        notificationRepository.deleteAllByNotifiedIdAndSenderIdAndTypeNot(friendId,
                user.getId(), NotificationType.MESSAGE);

        if (userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), friendId, UserStatus.FRIEND)) {

            UserUser userUser = userUserRepository.findByFirstUserIdAndSecondUserId(
                    user.getId(), friendId);
            userUserRepository.delete(userUser);
            return;
        }

        UserUser userUser = userUserRepository.findByFirstUserIdAndSecondUserId(
                friendId, user.getId());
        userUserRepository.delete(userUser);


    }

    public void updateProfile(@NotNull User user, ProfileUpdateForm profileUpdateForm) {
        user.updateUser(profileUpdateForm);
        userRepository.save(user);
    }

    public void changePassword(@NotNull ChangePasswordForm passwordForm, User user) {
        if (!passwordForm.newPassword().equals(passwordForm.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passwords do not match");
        }

        if (passwordEncoder.matches(passwordForm.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "New password must be different from old password");
        }

        user.setPassword(passwordEncoder.encode(passwordForm.newPassword()));
        userRepository.save(user);
    }

    public Optional<UserInfo> searchUserById(Long userId) {
        User userInfo = userRepository.findById(userId).orElse(null);
        return userToDto(userInfo);
    }

    public Optional<UserInfo> searchUserByUsername(String username) {
        User userInfo = userRepository.findByUsername(username).orElse(null);
        return userToDto(userInfo);
    }

    public ExtendedUserInfo showMyProfile(@NotNull Long id) {
        User userInfo = userRepository.getReferenceById(id);
        return userToExtendedDto(userInfo);
    }

    public List<UserInfo> showMyFriends(Long userId) {
        List<User> confirmedFriendshipUsers = userUserRepository.findConfirmedFriendshipUsersByUserId(userId);
        List<User> initiatedFriendshipUsers = userUserRepository.findInitiatedFriendshipUsersByUserId(userId);
        List<User> users = new ArrayList<>(confirmedFriendshipUsers);
        users.addAll(initiatedFriendshipUsers);
        return usersToDto(users);
    }

    private boolean isInvited(Long firstUserId, Long secondUserId) {
        return notificationRepository.existsByNotifiedIdAndSenderIdAndType(
                firstUserId, secondUserId, NotificationType.FRIENDSHIP) ||
                notificationRepository.existsByNotifiedIdAndSenderIdAndType(
                        secondUserId, firstUserId, NotificationType.FRIENDSHIP);
    }

    private boolean isFriend(Long firstUserId, Long secondUserId) {
        return userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                firstUserId, secondUserId, UserStatus.FRIEND) ||
                userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                        secondUserId, firstUserId, UserStatus.FRIEND);
    }

    private boolean isEitherUserBlocked(Long firstUserId, Long secondUserId) {
        return isBlocked(firstUserId, secondUserId) ||
                isBlocked(secondUserId, firstUserId);
    }

    private boolean isBlocked(Long firstUserId, Long secondUserId) {
        return userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                firstUserId, secondUserId, UserStatus.BLOCKED);
    }

    private Optional<UserInfo> userToDto(User user) {
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(new UserInfo(user));
    }

    @Contract("_ -> new")
    private @NotNull ExtendedUserInfo userToExtendedDto(User user) {
        return new ExtendedUserInfo(user);
    }

    private List<UserInfo> usersToDto(@NotNull List<User> users) {
        return users.stream()
                .map(UserInfo::new)
                .collect(Collectors.toList());
    }
}


