package com.qbank.question.domain;

import com.qbank.tag.domain.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "question_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public static QuestionTag of(Question question, Tag tag) {
        QuestionTag qt = new QuestionTag();
        qt.question = question;
        qt.tag = tag;
        return qt;
    }
}
