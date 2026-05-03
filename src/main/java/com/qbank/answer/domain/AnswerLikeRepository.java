package com.qbank.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {
    boolean existsByAnswerIdAndUserId(Long answerId, Long userId);
    long countByAnswerId(Long answerId);
    void deleteByAnswerIdAndUserId(Long answerId, Long userId);

    @Query("SELECT al.answerId AS answerId, COUNT(al) AS count FROM AnswerLike al WHERE al.answerId IN :answerIds GROUP BY al.answerId")
    List<AnswerLikeCountProjection> countByAnswerIdIn(@Param("answerIds") List<Long> answerIds);

    @Query("SELECT al.answerId FROM AnswerLike al WHERE al.userId = :userId AND al.answerId IN :answerIds")
    List<Long> findLikedAnswerIds(@Param("userId") Long userId, @Param("answerIds") List<Long> answerIds);
}
