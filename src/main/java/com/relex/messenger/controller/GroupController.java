package com.relex.messenger.controller;

import com.relex.messenger.dto.GroupForm;
import com.relex.messenger.dto.GroupInfo;
import com.relex.messenger.dto.ParticipantInfo;
import com.relex.messenger.entity.User;
import com.relex.messenger.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/")
    public ResponseEntity<?> createGroup(@RequestBody @Valid GroupForm groupForm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) authentication.getPrincipal();

        groupService.createGroup(creator, groupForm);
        return ResponseEntity.ok("Group created");
    } //checked

    @DeleteMapping("/{groupId}/")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        groupService.deleteGroup(groupId, admin);
        return ResponseEntity.ok("You have deleted your group");
    } //checked

    @PostMapping("/{groupId}/members/me")
    public ResponseEntity<?> joinGroup(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        groupService.joinGroup(groupId, admin);
        return ResponseEntity.ok("You have joined the group");
    } //checked

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        groupService.leaveGroup(groupId, user);
        return ResponseEntity.ok("You have left the group");

    } //checked

    @PatchMapping("/{groupId}/blocked-users")
    public ResponseEntity<?> banUser(@PathVariable Long groupId,
                                     @RequestParam Long banningUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        groupService.banUser(groupId, banningUserId, admin);
        return ResponseEntity.ok("You have banned the user");
    } //checked

    @DeleteMapping("/{groupId}/blocked-users")
    public ResponseEntity<?> unbanUser(@PathVariable Long groupId,
                                       @RequestParam Long unbanningUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        groupService.unbanUser(groupId, unbanningUserId, admin);
        return ResponseEntity.ok("You have unbanned this user");
    } //checked

    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> inviteUserToGroup(@PathVariable Long groupId,
                                               @RequestParam Long invitingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User inviter = (User) authentication.getPrincipal();

        groupService.inviteUserToGroup(groupId, invitingUserId, inviter);
        return ResponseEntity.ok("You have invited this user");
    } //checked

    @PatchMapping("/{groupId}/description")
    public ResponseEntity<?> changeDescription(@RequestBody String description,
                                               @PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        groupService.changeDescription(description, groupId, admin);
        return ResponseEntity.ok("Description has been changed");
    } //checked

    @GetMapping("/")
    public ResponseEntity<?> getMyGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<GroupInfo> groupsInfo = groupService.showMyGroups(user.getId());
        if (groupsInfo.isEmpty()) {
            return ResponseEntity.ok("You are not member of any group");
        }
        return ResponseEntity.ok(groupsInfo);

    } //checked

    @GetMapping("/search")
    public ResponseEntity<?> searchGroup(@RequestParam Long groupId) {
        Optional<GroupInfo> groupInfo = groupService.searchGroup(groupId);
        return ResponseEntity.ok(groupInfo);
    } //checked

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> showGroupParticipants(@PathVariable Long groupId) {
        List<ParticipantInfo> participants = groupService.showGroupParticipants(groupId);
        return ResponseEntity.ok(participants);
    } //checked
}

