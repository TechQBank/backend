package com.qbank.review.presentation;

import com.qbank.auth.SecurityUtils;
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
    public ReviewResponse getReview(@PathVariable Long questionId) {
        return reviewService.getReview(questionId, SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    public ReviewResponse saveReview(
            @PathVariable Long questionId,
            @Valid @RequestBody ReviewRequest request) {

        return reviewService.saveReview(questionId, request, SecurityUtils.getCurrentUserId());
    }
}
