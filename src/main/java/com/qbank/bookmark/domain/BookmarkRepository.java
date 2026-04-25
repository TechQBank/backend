package com.qbank.bookmark.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    @Query("""
            SELECT b.questionId AS questionId, COUNT(b) AS count
            FROM Bookmark b
            WHERE b.questionId IN :questionIds
            GROUP BY b.questionId
            """)
    List<BookmarkCountProjection> countByQuestionIdIn(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT b.questionId FROM Bookmark b WHERE b.userId = :userId AND b.questionId IN :questionIds")
    List<Long> findBookmarkedQuestionIds(@Param("userId") Long userId,
                                         @Param("questionIds") List<Long> questionIds);

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
