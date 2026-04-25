package com.qbank.answer.domain;

import com.qbank.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user_question_answers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQuestionAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int version;

    @OneToMany(mappedBy = "answer")
    private List<AnswerHistory> histories = new ArrayList<>();

    public static UserQuestionAnswer create(Long userId, Long questionId, String content) {
        UserQuestionAnswer answer = new UserQuestionAnswer();
        answer.userId = userId;
        answer.questionId = questionId;
        answer.content = content;
        answer.version = 1;
        return answer;
    }

    public void updateContent(String newContent) {
        this.version++;
        this.content = newContent;
        this.histories.add(AnswerHistory.of(this, newContent, this.version));
    }
}
