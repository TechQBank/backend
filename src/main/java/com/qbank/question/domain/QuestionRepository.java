package com.qbank.question.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long>,
        JpaSpecificationExecutor<Question> {

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
}
