package com.qbank.review.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<UserQuestionReview, Long> {
    Optional<UserQuestionReview> findByUserIdAndQuestionId(Long userId, Long questionId);
}
