package com.qbank.review.application;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.domain.Question;
import com.qbank.question.domain.QuestionRepository;
import com.qbank.question.domain.Visibility;
import com.qbank.review.application.dto.ReviewRequest;
import com.qbank.review.application.dto.ReviewResponse;
import com.qbank.review.domain.ReviewLog;
import com.qbank.review.domain.ReviewLogRepository;
import com.qbank.review.domain.ReviewRepository;
import com.qbank.review.domain.ReviewStatus;
import com.qbank.review.domain.UserQuestionReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final QuestionRepository questionRepository;

    public ReviewResponse getReview(Long questionId, Long userId) {
        validateAccess(questionId, userId);
        return reviewRepository.findByUserIdAndQuestionId(userId, questionId)
                .map(r -> new ReviewResponse(r.getStatus()))
                .orElse(new ReviewResponse(null));
    }

    @Transactional
    public ReviewResponse saveReview(Long questionId, ReviewRequest request, Long userId) {
        validateAccess(questionId, userId);

        ReviewStatus newStatus = request.getStatus();
        if (newStatus == null) {
            throw new BusinessException(ErrorCode.REVIEW_STATUS_INVALID);
        }

        Optional<UserQuestionReview> existing = reviewRepository.findByUserIdAndQuestionId(userId, questionId);

        UserQuestionReview review;
        if (existing.isPresent()) {
            review = existing.get();
            review.changeStatus(newStatus);
        } else {
            review = UserQuestionReview.create(userId, questionId, newStatus);
            reviewRepository.save(review);
        }

        reviewLogRepository.save(ReviewLog.of(review, newStatus));

        return new ReviewResponse(review.getStatus());
    }

    private void validateAccess(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        if (question.getVisibility() == Visibility.PRIVATE
                && !Objects.equals(question.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }
}
