package com.qbank.group.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    List<QuestionGroup> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<QuestionGroup> findByUserIdAndIsPublicTrueOrderByCreatedAtDesc(Long userId);
}
