package com.qbank.question.presentation;

import com.qbank.bookmark.application.BookmarkService;
import com.qbank.bookmark.application.dto.BookmarkToggle;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.application.QuestionService;
import com.qbank.question.application.dto.QuestionDetail;
import com.qbank.question.application.dto.RegisterQuestion;
import com.qbank.question.presentation.dto.MyQuestions;
import com.qbank.question.presentation.dto.PublicQuestions;
import com.qbank.question.presentation.dto.QuestionStats;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        // BOOKMARK_COUNT 정렬 시 Pageable에 sort 속성을 넣으면 쿼리 ORDER BY와 충돌하므로 제외
        Pageable pageable = dto.isBookmarkSort()
                ? PageRequest.of(page, size)
                : PageRequest.of(page, size, Sort.by("createdAt").descending());

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

    @PutMapping("/{id}")
    public void update(
            @PathVariable Long id,
            @Valid @RequestBody RegisterQuestion.Request request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        questionService.update(id, request, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        questionService.delete(id, userId);
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

    @GetMapping("/my")
    public Page<PublicQuestions.Response> getMyQuestions(
            MyQuestions.Request params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Pageable pageable = PageRequest.of(page, size, params.toSort());
        return questionService.getMyQuestions(params.getVisibility(), pageable, userId)
                .map(PublicQuestions.Response::of);
    }

    @GetMapping("/bookmarks")
    public Page<PublicQuestions.Response> getBookmarkedQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return questionService.getBookmarkedQuestions(pageable, userId)
                .map(PublicQuestions.Response::of);
    }

    @GetMapping("/stats")
    public QuestionStats.Response getStats(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        return QuestionStats.Response.of(questionService.getStats(userId));
    }
}
