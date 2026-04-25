package com.qbank.answer.presentation;

import com.qbank.answer.application.AnswerService;
import com.qbank.answer.application.dto.AnswerHistoryResponse;
import com.qbank.answer.application.dto.AnswerRequest;
import com.qbank.answer.application.dto.AnswerResponse;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
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
    public AnswerResponse getAnswer(
            @PathVariable Long questionId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return answerService.getAnswer(questionId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerResponse createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return answerService.createAnswer(questionId, request, userId);
    }

    @PutMapping
    public AnswerResponse updateAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return answerService.updateAnswer(questionId, request, userId);
    }

    @GetMapping("/history")
    public List<AnswerHistoryResponse> getHistory(
            @PathVariable Long questionId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return answerService.getHistory(questionId, userId);
    }
}
