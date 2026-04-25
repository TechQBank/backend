package com.qbank.user.presentation;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.user.application.UserService;
import com.qbank.user.application.dto.UpdateProfileRequest;
import com.qbank.user.application.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return userService.getMyProfile(userId);
    }

    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return userService.updateMyProfile(userId, request);
    }
}
