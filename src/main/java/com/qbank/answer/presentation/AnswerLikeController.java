package com.qbank.answer.presentation;

import com.qbank.answer.application.AnswerService;
import com.qbank.answer.application.dto.LikeResponse;
import com.qbank.auth.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers/{answerId}/like")
@RequiredArgsConstructor
public class AnswerLikeController {

    private final AnswerService answerService;

    @PostMapping
    public LikeResponse likeAnswer(@PathVariable Long answerId) {
        return answerService.likeAnswer(answerId, SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping
    public LikeResponse unlikeAnswer(@PathVariable Long answerId) {
        return answerService.unlikeAnswer(answerId, SecurityUtils.getCurrentUserId());
    }
}
