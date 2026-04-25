package com.qbank.review.domain;

import com.qbank.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user_question_reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQuestionReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

    @OneToMany(mappedBy = "review")
    private List<ReviewLog> logs = new ArrayList<>();

    public static UserQuestionReview create(Long userId, Long questionId, ReviewStatus status) {
        UserQuestionReview review = new UserQuestionReview();
        review.userId = userId;
        review.questionId = questionId;
        review.status = status;
        review.logs.add(ReviewLog.of(review, status));
        return review;
    }

    public void changeStatus(ReviewStatus newStatus) {
        this.status = newStatus;
        this.logs.add(ReviewLog.of(this, newStatus));
    }
}
