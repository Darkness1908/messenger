package com.relex.messenger.service;

import com.relex.messenger.dto.GroupForm;
import com.relex.messenger.dto.GroupInfo;
import com.relex.messenger.dto.ParticipantInfo;
import com.relex.messenger.entity.Group;
import com.relex.messenger.entity.Notification;
import com.relex.messenger.entity.User;
import com.relex.messenger.entity.UserGroup;
import com.relex.messenger.enums.GroupStatus;
import com.relex.messenger.enums.NotificationType;
import com.relex.messenger.enums.UserStatus;
import com.relex.messenger.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final NotificationRepository notificationRepository;
    private final UserUserRepository userUserRepository;

    @Transactional
    public void createGroup(User creator, GroupForm groupForm) {
        Group group = new Group(groupForm, creator);
        UserGroup userGroup = new UserGroup(creator, group, GroupStatus.JOINED);
        userGroupRepository.save(userGroup);
        groupRepository.save(group);
    }

    public void deleteGroup(Long groupId, User admin) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (isUserNotAdministrator(admin, groupId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an administrator of this group to do this operation");
        }
        Group group = groupRepository.getReferenceById(groupId);
        groupRepository.delete(group);
    }

    public void unbanUser(Long groupId, Long unbanningUserId, User admin) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (!userRepository.existsById(unbanningUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (isUserNotAdministrator(admin, groupId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an administrator of this group");
        }

        if (admin.getId().equals(unbanningUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't unban yourself");
        }

        if (!isBanned(unbanningUserId, groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user is not banned");
        }

        UserGroup userGroup = userGroupRepository.getByUserIdAndGroupId(unbanningUserId, groupId);
        userGroupRepository.delete(userGroup);
    }

    @Transactional
    public void banUser(Long groupId, Long banningUserId, User admin) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (!userRepository.existsById(banningUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (isUserNotAdministrator(admin, groupId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an administrator of this group to do this operation");
        }

        if (admin.getId().equals(banningUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't ban yourself");
        }

        if (isBanned(banningUserId, groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user is already banned");
        }

        if (isMember(banningUserId, groupId)) {
            UserGroup userGroup = userGroupRepository.getByUserIdAndGroupId(banningUserId, groupId);
            userGroup.setGroupStatus(GroupStatus.BANNED);
            Group group = groupRepository.getReferenceById(groupId);
            group.setNumberOfParticipants(group.getNumberOfParticipants() - 1L);
            groupRepository.save(group);
            return;
        }

        if (isInvited(banningUserId, groupId)) {
            Notification notification = notificationRepository.getByNotifiedIdAndGroupId(banningUserId, groupId);
            notificationRepository.delete(notification);
        }

        User banningUser = userRepository.getReferenceById(banningUserId);
        Group group = groupRepository.getReferenceById(groupId);
        UserGroup userGroup = new UserGroup(banningUser, group, GroupStatus.BANNED);
        userGroupRepository.save(userGroup);
    }

    @Transactional
    public void leaveGroup(Long groupId, User user) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (isBanned(user.getId(), groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You are banned in this group");
        }

        if (!isMember(user.getId(), groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You can't leave a group you are not member of");
        }

        if (isUserNotAdministrator(user, groupId)) {
            UserGroup userGroup = userGroupRepository.getByUserIdAndGroupId(user.getId(), groupId);
            Group group = groupRepository.getReferenceById(groupId);
            group.setNumberOfParticipants(group.getNumberOfParticipants() - 1L);
            userGroupRepository.delete(userGroup);
            return;
        }
        Group group = groupRepository.getReferenceById(groupId);
        groupRepository.delete(group);
    }

    @Transactional
    public void joinGroup(Long groupId, User user) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (isBanned(user.getId(), groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You are banned in this group");
        }

        if (isMember(user.getId(), groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You are already a member of the group");
        }

        User enteringUser = userRepository.getReferenceById(user.getId());
        Group group = groupRepository.getReferenceById(groupId);
        group.setNumberOfParticipants(group.getNumberOfParticipants() + 1L);
        UserGroup userGroup = new UserGroup(enteringUser, group, GroupStatus.JOINED);
        userGroupRepository.save(userGroup);
    }

    public void changeDescription(String description, Long groupId, User admin) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (isUserNotAdministrator(admin, groupId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an administrator of this group");
        }

        Group group = groupRepository.getReferenceById(groupId);
        group.setDescription(description);
        groupRepository.save(group);
    }

    public void inviteUserToGroup(Long groupId, Long invitingUserId, User inviter) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Group not found");
        }

        if (!userRepository.existsById(invitingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (!isMember(inviter.getId(), groupId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not a member of the group");
        }

        if (inviter.getId().equals(invitingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't add yourself");
        }

        if (isMember(invitingUserId, groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User is already a member of the group");
        }

        if (isInvited(invitingUserId, groupId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user is already invited");
        }

        if (!isFriend(invitingUserId, inviter.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can invite only friends to a group");
        }

        User invited = userRepository.getReferenceById(invitingUserId);
        Group group = groupRepository.getReferenceById(groupId);
        Notification notification = new Notification(invited, inviter, NotificationType.GROUP, group);
        notificationRepository.save(notification);
    }

    public Optional<GroupInfo> searchGroup(Long groupId) {
        Group groupInfo = groupRepository.findById(groupId).orElse(null);
        return groupToDto(groupInfo);
    }

    public List<GroupInfo> showMyGroups(Long userId) {
        List<Group> groups = userGroupRepository.findGroupsByUserIdAndStatus(userId, GroupStatus.JOINED);
        return groupsToDto(groups);
    }

    public List<ParticipantInfo> showGroupParticipants(Long groupId) {
        List<User> participants = userGroupRepository.findUsersByGroupIdAndStatus(groupId, GroupStatus.JOINED);
        return participantsToDto(participants);
    }

    private List<ParticipantInfo> participantsToDto(@NotNull List<User> participants) {
        return participants.stream()
                .map(ParticipantInfo::new)
                .collect(Collectors.toList());
    }

    private List<GroupInfo> groupsToDto(List<Group> groups) {
        return groups.stream()
                .map(GroupInfo::new)
                .collect(Collectors.toList());
    }

    private Optional<GroupInfo> groupToDto(Group group) {
        if (group == null)
        {
            return Optional.empty();
        }
        return Optional.of(new GroupInfo(group));
    }

    private boolean isInvited(Long userId, Long groupId) {
        return notificationRepository.existsByNotifiedIdAndGroupId(userId, groupId);
    }

    private boolean isMember(Long userId, Long groupId) {
        return userGroupRepository.existsByUserIdAndGroupIdAndGroupStatus(userId, groupId, GroupStatus.JOINED);
    }

    private boolean isBanned(Long userId, Long groupId) {
        return userGroupRepository.existsByUserIdAndGroupIdAndGroupStatus(userId, groupId, GroupStatus.BANNED);
    }

    private boolean isFriend(Long firstUserId, Long secondUserId) {
        return userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                firstUserId, secondUserId, UserStatus.FRIEND) ||
                userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                        secondUserId, firstUserId, UserStatus.FRIEND);
    }

    private boolean isUserNotAdministrator(User user, Long groupId) {
        return !groupRepository.existsByIdAndAdministrator(groupId, user);
    }
}
