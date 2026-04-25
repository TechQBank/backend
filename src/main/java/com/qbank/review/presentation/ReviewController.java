package com.qbank.review.presentation;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.review.application.ReviewService;
import com.qbank.review.application.dto.ReviewRequest;
import com.qbank.review.application.dto.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions/{questionId}/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ReviewResponse getReview(
            @PathVariable Long questionId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return reviewService.getReview(questionId, userId);
    }

    @PostMapping
    public ReviewResponse saveReview(
            @PathVariable Long questionId,
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return reviewService.saveReview(questionId, request, userId);
    }
}
