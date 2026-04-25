package com.qbank.review.domain;

import com.qbank.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLog extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private UserQuestionReview review;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    public static ReviewLog of(UserQuestionReview review, ReviewStatus status) {
        ReviewLog log = new ReviewLog();
        log.review = review;
        log.status = status;
        return log;
    }
}
