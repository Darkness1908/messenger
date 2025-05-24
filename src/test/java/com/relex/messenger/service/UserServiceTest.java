package com.relex.messenger.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.relex.messenger.entity.User;
import com.relex.messenger.entity.UserUser;
import com.relex.messenger.enums.NotificationType;
import com.relex.messenger.enums.UserStatus;
import com.relex.messenger.repository.NotificationRepository;
import com.relex.messenger.repository.UserRepository;
import com.relex.messenger.repository.UserUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserUserRepository userUserRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserUser userUser;

    @InjectMocks
    private UserService userService;

    private User user;
    private User friend;


    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        friend = new User();
        friend.setId(2L);
    }


    @Test
    public void testDeleteFriend_UserNotFound() {
        when(userRepository.existsById(friend.getId())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteFriend(friend.getId(), user);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    public void testDeleteFriend_CannotDeleteYourself() {
        user.setId(1L);
        friend.setId(1L);

        when(userRepository.existsById(eq(friend.getId()))).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteFriend(friend.getId(), user);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You cannot delete yourself from friends", exception.getReason());
    }


    @Test
    public void testDeleteFriend_NotAFriend() {
        when(userRepository.existsById(friend.getId())).thenReturn(true);
        when(userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), friend.getId(), UserStatus.FRIEND) ||
                userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                        friend.getId(), user.getId(), UserStatus.FRIEND)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteFriend(friend.getId(), user);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("This user is not your friend", exception.getReason());
    }


    @Test
    public void testDeleteFriend_SuccessOne() {
        when(userRepository.existsById(friend.getId())).thenReturn(true);
        when(userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), friend.getId(), UserStatus.FRIEND) ||
                userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                        friend.getId(), user.getId(), UserStatus.FRIEND)).thenReturn(true);

        when(userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), friend.getId(), UserStatus.FRIEND)).thenReturn(true);

        when(userUserRepository.findByFirstUserIdAndSecondUserId(user.getId(), friend.getId()))
                .thenReturn(userUser);

        userService.deleteFriend(friend.getId(), user);

        verify(notificationRepository).deleteAllByNotifiedIdAndSenderIdAndTypeNot(user.getId(),
                friend.getId(), NotificationType.MESSAGE);

        verify(notificationRepository).deleteAllByNotifiedIdAndSenderIdAndTypeNot(friend.getId(),
                user.getId(), NotificationType.MESSAGE);

        verify(userUserRepository).delete(userUser);
    }

    @Test
    public void testDeleteFriend_SuccessSecond() {
        when(userRepository.existsById(friend.getId())).thenReturn(true);
        when(userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), friend.getId(), UserStatus.FRIEND) ||
                userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                        friend.getId(), user.getId(), UserStatus.FRIEND)).thenReturn(true);

        when(userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                user.getId(), friend.getId(), UserStatus.FRIEND)).thenReturn(false);

        when(userUserRepository.findByFirstUserIdAndSecondUserId(friend.getId(), user.getId()))
                .thenReturn(userUser);

        userService.deleteFriend(friend.getId(), user);

        verify(notificationRepository).deleteAllByNotifiedIdAndSenderIdAndTypeNot(user.getId(),
                friend.getId(), NotificationType.MESSAGE);

        verify(notificationRepository).deleteAllByNotifiedIdAndSenderIdAndTypeNot(friend.getId(),
                user.getId(), NotificationType.MESSAGE);

        verify(userUserRepository).delete(userUser);
    }

}
