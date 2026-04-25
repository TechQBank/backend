package com.qbank.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<UserQuestionAnswer, Long> {
    Optional<UserQuestionAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
}
