package com.qbank.answer.presentation;

import com.qbank.answer.application.AnswerService;
import com.qbank.answer.application.dto.PublicAnswerResponse;
import com.qbank.auth.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/public-answers")
@RequiredArgsConstructor
public class PublicAnswerController {

    private final AnswerService answerService;

    @GetMapping
    public List<PublicAnswerResponse> getPublicAnswers(@PathVariable Long questionId) {
        return answerService.getPublicAnswers(questionId, SecurityUtils.getCurrentUserId());
    }
}
