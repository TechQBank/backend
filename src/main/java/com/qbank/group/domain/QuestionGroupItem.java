package com.qbank.group.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "question_group_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionGroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    public static QuestionGroupItem of(Long groupId, Long questionId) {
        QuestionGroupItem item = new QuestionGroupItem();
        item.groupId = groupId;
        item.questionId = questionId;
        item.addedAt = LocalDateTime.now();
        return item;
    }
}
