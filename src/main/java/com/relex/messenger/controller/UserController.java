package com.relex.messenger.controller;

import com.relex.messenger.service.JwtBlacklistService;
import com.relex.messenger.dto.*;
import com.relex.messenger.entity.User;
import com.relex.messenger.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        jwtBlacklistService.addTokenToBlacklist(token);
        return ResponseEntity.ok().build();
    } //checked

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        String token = request.getHeader("Authorization").substring(7);
        userService.deleteAccount(user, token);
        return ResponseEntity.ok("Account has been deleted." +
                " You can restore your account within 30 days from the date of deletion");
    } //checked

    @GetMapping("/my-friends")
    public ResponseEntity<?> getMyFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<UserInfo> usersInfo = userService.showMyFriends(user.getId());
        if (usersInfo.isEmpty()) {
            return ResponseEntity.ok("No friends");
        }

        return ResponseEntity.ok(usersInfo);
    } //checked???

    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ExtendedUserInfo userInfo = userService.showMyProfile(user.getId());

        return ResponseEntity.ok(userInfo);
    } //checked

    @PatchMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ProfileUpdateForm profileUpdateForm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userService.updateProfile(user, profileUpdateForm);

        return ResponseEntity.ok("Your profile has been updated");
    } //checked

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
           @Valid @RequestBody ChangePasswordForm passwordForm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userService.changePassword(passwordForm, user);
        return ResponseEntity.ok("Password has been changed");
    } // checked

    @PostMapping("/blocked-users/{blockingUserId}")
    public ResponseEntity<?> blockUser(@PathVariable Long blockingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userService.blockUser(blockingUserId, user);
        return ResponseEntity.ok("User has been blocked");
    } //checked

    @DeleteMapping("/blocked-users/{unblockingUserId}")
    public ResponseEntity<?> unblockUser(@PathVariable Long unblockingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userService.unblockUser(unblockingUserId, user);
        return ResponseEntity.ok("User has been unblocked");
    } //checked

    @PostMapping("/friends/{friendId}")
    public ResponseEntity<?> inviteToFriends(@PathVariable Long friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userService.inviteToFriends(friendId, user);
        return ResponseEntity.ok("Friend request has been sent");
    } //checked

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userService.deleteFriend(friendId, user);
        return ResponseEntity.ok("Friend has been deleted");
    } //checked

    @GetMapping("/search/id")
    public ResponseEntity<?> searchUserById(@RequestParam Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<UserInfo> userInfo = userService.searchUserById(userId);
        return ResponseEntity.ok(userInfo);
    } //checked

    @GetMapping("/search/username")
    public ResponseEntity<?> searchUserByUsername(@RequestParam String username) {
        Optional<UserInfo> userInfo = userService.searchUserByUsername(username);
        return ResponseEntity.ok(userInfo);
    } //checked
}
