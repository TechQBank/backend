package com.qbank.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<UserQuestionAnswer, Long> {
    Optional<UserQuestionAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    @Query("SELECT a.questionId FROM UserQuestionAnswer a WHERE a.userId = :userId AND a.questionId IN :questionIds")
    List<Long> findAnsweredQuestionIds(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);

    @Query("""
            SELECT a FROM UserQuestionAnswer a
            WHERE a.questionId = :questionId AND a.isPublic = true
            ORDER BY (SELECT COUNT(al) FROM AnswerLike al WHERE al.answerId = a.id) DESC
            """)
    List<UserQuestionAnswer> findPublicAnswersByQuestionId(@Param("questionId") Long questionId);

    long countByQuestionIdAndIsPublicTrueAndUserIdNot(Long questionId, Long userId);
}
