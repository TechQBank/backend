package com.qbank.question.presentation;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.application.QuestionService;
import com.qbank.question.application.dto.StudyQuestionSummary;
import com.qbank.question.presentation.dto.StudyQuestions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions/study")
@RequiredArgsConstructor
public class StudyController {

    private final QuestionService questionService;

    @GetMapping
    public Page<StudyQuestionSummary> getStudyQuestions(
            StudyQuestions.Request params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return questionService.getStudyQuestions(
                params.getReviewStatus(), params.tagIds(), params.hasAnswer(),
                params.isMine(), params.isBookmarked(), params.getVisibility(),
                pageable, userId);
    }
}
