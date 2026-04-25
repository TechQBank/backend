package com.qbank.question.presentation;

import com.qbank.bookmark.application.BookmarkService;
import com.qbank.bookmark.application.dto.BookmarkToggle;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.application.QuestionService;
import com.qbank.question.application.dto.QuestionDetail;
import com.qbank.question.application.dto.RegisterQuestion;
import com.qbank.question.presentation.dto.PublicQuestions;
import com.qbank.question.presentation.dto.QuestionStats;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final BookmarkService bookmarkService;

    @GetMapping
    public Page<PublicQuestions.Response> getPublicQuestions(
            PublicQuestions.Request dto,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        return questionService.getPublicQuestions(dto.to(pageable, userId)).map(PublicQuestions.Response::of);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterQuestion.Response register(
            @Valid @RequestBody RegisterQuestion.Request request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return questionService.register(request, userId);
    }

    @GetMapping("/{id}")
    public QuestionDetail.Response getDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        return questionService.getDetail(id, userId);
    }

    @PostMapping("/{id}/bookmark")
    public BookmarkToggle.Response toggleBookmark(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return bookmarkService.toggle(id, userId);
    }

    @GetMapping("/stats")
    public QuestionStats.Response getStats(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        return QuestionStats.Response.of(questionService.getStats(userId));
    }
}
