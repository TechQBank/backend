package com.qbank.bookmark.application;

import com.qbank.bookmark.application.dto.BookmarkToggle;
import com.qbank.bookmark.domain.Bookmark;
import com.qbank.bookmark.domain.BookmarkRepository;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.domain.Question;
import com.qbank.question.domain.QuestionRepository;
import com.qbank.question.domain.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public BookmarkToggle.Response toggle(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (question.getVisibility() == Visibility.PRIVATE
                && !question.getAuthorId().equals(userId)) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndQuestionId(userId, questionId);
        boolean isBookmarked;

        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            isBookmarked = false;
        } else {
            bookmarkRepository.save(Bookmark.of(userId, questionId));
            isBookmarked = true;
        }

        long count = bookmarkRepository.countByQuestionId(questionId);
        return new BookmarkToggle.Response(isBookmarked, count);
    }
}
