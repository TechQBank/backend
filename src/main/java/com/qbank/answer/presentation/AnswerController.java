package com.qbank.answer.presentation;

import com.qbank.answer.application.AnswerService;
import com.qbank.answer.application.dto.AnswerHistoryResponse;
import com.qbank.answer.application.dto.AnswerRequest;
import com.qbank.answer.application.dto.AnswerResponse;
import com.qbank.auth.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping
    public AnswerResponse getAnswer(@PathVariable Long questionId) {
        return answerService.getAnswer(questionId, SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerResponse createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerRequest request) {

        return answerService.createAnswer(questionId, request, SecurityUtils.getCurrentUserId());
    }

    @PutMapping
    public AnswerResponse updateAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerRequest request) {

        return answerService.updateAnswer(questionId, request, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/history")
    public List<AnswerHistoryResponse> getHistory(@PathVariable Long questionId) {
        return answerService.getHistory(questionId, SecurityUtils.getCurrentUserId());
    }
}
