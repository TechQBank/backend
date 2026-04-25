package com.qbank.answer.domain;

import com.qbank.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "answer_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerHistory extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private UserQuestionAnswer answer;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int version;

    public static AnswerHistory of(UserQuestionAnswer answer, String content, int version) {
        AnswerHistory history = new AnswerHistory();
        history.answer = answer;
        history.content = content;
        history.version = version;
        return history;
    }
}
