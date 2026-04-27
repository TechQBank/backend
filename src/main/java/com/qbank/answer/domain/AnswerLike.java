package com.qbank.answer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "answer_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long answerId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static AnswerLike of(Long answerId, Long userId) {
        AnswerLike like = new AnswerLike();
        like.answerId = answerId;
        like.userId = userId;
        like.createdAt = LocalDateTime.now();
        return like;
    }
}
