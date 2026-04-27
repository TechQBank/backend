package com.qbank.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {
    boolean existsByAnswerIdAndUserId(Long answerId, Long userId);
    long countByAnswerId(Long answerId);
    void deleteByAnswerIdAndUserId(Long answerId, Long userId);
}
