package com.qbank.question.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    long countByAuthorId(Long authorId);
    long countByVisibility(Visibility visibility);

    @Query(
            value = """
                    SELECT q FROM Question q
                    WHERE q.visibility = 'PUBLIC'
                    ORDER BY (SELECT COUNT(b) FROM Bookmark b WHERE b.questionId = q.id) DESC
                    """,
            countQuery = "SELECT COUNT(q) FROM Question q WHERE q.visibility = 'PUBLIC'"
    )
    Page<Question> findPublicByBookmarkCount(Pageable pageable);

    @Query(
            value = """
                    SELECT q FROM Question q
                    WHERE q.authorId IN (
                        SELECT f.followeeId FROM Follow f WHERE f.followerId = :userId
                    )
                    AND q.visibility = 'PUBLIC'
                    ORDER BY q.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(q) FROM Question q
                    WHERE q.authorId IN (
                        SELECT f.followeeId FROM Follow f WHERE f.followerId = :userId
                    )
                    AND q.visibility = 'PUBLIC'
                    """
    )
    Page<Question> findFeedQuestions(@Param("userId") Long userId, Pageable pageable);
}
