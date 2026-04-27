package com.qbank.question.presentation;

import com.qbank.auth.SecurityUtils;
import com.qbank.common.response.SliceResponse;
import com.qbank.question.application.QuestionService;
import com.qbank.question.application.dto.StudyQuestionSummary;
import com.qbank.question.presentation.dto.StudyQuestions;
import lombok.RequiredArgsConstructor;
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
    public SliceResponse<StudyQuestionSummary> getStudyQuestions(
            StudyQuestions.Request params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return questionService.getStudyQuestions(
                params.getReviewStatus(), params.tagIds(), params.hasAnswer(),
                params.isMine(), params.isBookmarked(), params.getVisibility(),
                pageable, userId);
    }
}
