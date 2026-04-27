package com.qbank.user.presentation;

import com.qbank.auth.SecurityUtils;
import com.qbank.follow.application.FollowService;
import com.qbank.follow.application.dto.FollowResponse;
import com.qbank.user.application.UserService;
import com.qbank.user.application.dto.PublicUserResponse;
import com.qbank.user.application.dto.UpdateProfileRequest;
import com.qbank.user.application.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile() {
        return userService.getMyProfile(SecurityUtils.getCurrentUserId());
    }

    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateMyProfile(SecurityUtils.getCurrentUserId(), request);
    }

    @GetMapping("/search")
    public List<PublicUserResponse> searchUsers(@RequestParam String nickname) {
        return userService.searchUsers(nickname, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/{userId}")
    public PublicUserResponse getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId, SecurityUtils.getCurrentUserId());
    }

    @PostMapping("/{userId}/follow")
    public FollowResponse follow(@PathVariable Long userId) {
        return followService.follow(SecurityUtils.getCurrentUserId(), userId);
    }

    @DeleteMapping("/{userId}/follow")
    public FollowResponse unfollow(@PathVariable Long userId) {
        return followService.unfollow(SecurityUtils.getCurrentUserId(), userId);
    }

    @GetMapping("/{userId}/followers")
    public List<PublicUserResponse> getFollowers(@PathVariable Long userId) {
        return userService.getFollowers(userId, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/{userId}/following")
    public List<PublicUserResponse> getFollowing(@PathVariable Long userId) {
        return userService.getFollowing(userId, SecurityUtils.getCurrentUserId());
    }
}
