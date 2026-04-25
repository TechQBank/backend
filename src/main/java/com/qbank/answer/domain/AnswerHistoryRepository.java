package com.qbank.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerHistoryRepository extends JpaRepository<AnswerHistory, Long> {
    List<AnswerHistory> findAllByAnswerIdOrderByVersionDesc(Long answerId);
}
