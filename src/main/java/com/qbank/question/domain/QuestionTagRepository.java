package com.qbank.question.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    @Query("SELECT qt FROM QuestionTag qt JOIN FETCH qt.tag WHERE qt.question.id IN :questionIds")
    List<QuestionTag> findAllWithTagByQuestionIdIn(@Param("questionIds") List<Long> questionIds);

    @Modifying
    @Query("DELETE FROM QuestionTag qt WHERE qt.question.id = :questionId")
    void deleteAllByQuestionId(@Param("questionId") Long questionId);
}
