package com.qbank.question.presentation;

import com.qbank.question.application.QuestionService;
import com.qbank.question.application.dto.QuestionStatsResponse;
import com.qbank.question.presentation.dto.PublicQuestions;
import com.qbank.question.presentation.dto.QuestionStats;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Page<PublicQuestions.Response> getPublicQuestions(
            PublicQuestions.Request dto,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        return questionService.getPublicQuestions(dto.to(pageable, userId)).map(PublicQuestions.Response::of);
    }

    @GetMapping("/stats")
    public QuestionStats.Response getStats(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        return QuestionStats.Response.of(questionService.getStats(userId));
    }
}
