package com.qbank.group.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionGroupItemRepository extends JpaRepository<QuestionGroupItem, Long> {
    List<QuestionGroupItem> findByGroupId(Long groupId);
    boolean existsByGroupIdAndQuestionId(Long groupId, Long questionId);
    void deleteByGroupIdAndQuestionId(Long groupId, Long questionId);
    void deleteAllByGroupId(Long groupId);
    long countByGroupId(Long groupId);

    @Query("SELECT i.groupId FROM QuestionGroupItem i WHERE i.questionId = :questionId")
    List<Long> findGroupIdsByQuestionId(@Param("questionId") Long questionId);
}
