package com.qbank.question.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuestionRepository extends JpaRepository<Question, Long>,
        JpaSpecificationExecutor<Question> {

    long countByAuthorId(Long authorId);
    long countByVisibility(Visibility visibility);
}
