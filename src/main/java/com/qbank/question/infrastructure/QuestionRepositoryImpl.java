package com.qbank.question.infrastructure;

import com.qbank.answer.domain.QUserQuestionAnswer;
import com.qbank.bookmark.domain.QBookmark;
import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.question.domain.*;
import com.qbank.review.domain.QUserQuestionReview;
import com.qbank.review.domain.ReviewStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QQuestion q = QQuestion.question;
    private static final QQuestionTag qt = QQuestionTag.questionTag;
    private static final QBookmark b = QBookmark.bookmark;
    private static final QUserQuestionAnswer a = QUserQuestionAnswer.userQuestionAnswer;
    private static final QUserQuestionReview r = QUserQuestionReview.userQuestionReview;

    @Override
    public List<Question> findPublicQuestions(QuestionSearchCondition condition, Pageable pageable) {
        return queryFactory
                .selectFrom(q)
                .where(
                        q.visibility.eq(Visibility.PUBLIC),
                        containsKeyword(condition.keyword()),
                        hasAnyTagId(condition.tagIds()),
                        hasCareerLevel(condition.careerLevel()),
                        hasDifficulty(condition.difficulty()),
                        hasAuthor(condition.authorId())
                )
                .orderBy(toOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Question> findMyQuestions(Long userId, Visibility visibility, Pageable pageable) {
        return queryFactory
                .selectFrom(q)
                .where(
                        q.authorId.eq(userId),
                        visibilityEq(visibility)
                )
                .orderBy(toOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Question> findBookmarkedQuestions(Long userId, Pageable pageable) {
        return queryFactory
                .selectFrom(q)
                .where(isBookmarkedByUser(userId))
                .orderBy(toOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Question> findStudyQuestions(Long userId, ReviewStatus reviewStatus, List<Long> tagIds,
                                              Boolean hasAnswer, Boolean isMine, Boolean isBookmarked,
                                              Visibility visibility, Pageable pageable) {
        return queryFactory
                .selectFrom(q)
                .where(
                        isStudyQuestion(userId),
                        hasReviewStatus(userId, reviewStatus),
                        hasAnyTagId(tagIds),
                        hasAnswerFilter(userId, hasAnswer),
                        isMineFilter(userId, isMine),
                        isBookmarkedFilter(userId, isBookmarked),
                        visibilityEq(visibility)
                )
                .orderBy(toOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    // ─── BooleanExpression helpers ─────────────────────────────────────────────

    private BooleanExpression containsKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        String like = "%" + keyword + "%";
        return q.title.like(like).or(q.description.like(like));
    }

    private BooleanExpression hasAnyTagId(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return null;
        return q.id.in(
                JPAExpressions.select(qt.question.id)
                        .from(qt)
                        .where(qt.tag.id.in(tagIds))
        );
    }

    private BooleanExpression hasCareerLevel(CareerLevel careerLevel) {
        return careerLevel != null ? q.careerLevel.eq(careerLevel) : null;
    }

    private BooleanExpression hasDifficulty(Difficulty difficulty) {
        return difficulty != null ? q.difficulty.eq(difficulty) : null;
    }

    private BooleanExpression hasAuthor(Long authorId) {
        return authorId != null ? q.authorId.eq(authorId) : null;
    }

    private BooleanExpression visibilityEq(Visibility visibility) {
        return visibility != null ? q.visibility.eq(visibility) : null;
    }

    private BooleanExpression isBookmarkedByUser(Long userId) {
        return q.id.in(
                JPAExpressions.select(b.questionId)
                        .from(b)
                        .where(b.userId.eq(userId))
        );
    }

    private BooleanExpression isStudyQuestion(Long userId) {
        return q.authorId.eq(userId)
                .or(q.id.in(JPAExpressions.select(b.questionId).from(b).where(b.userId.eq(userId))))
                .or(q.id.in(JPAExpressions.select(a.questionId).from(a).where(a.userId.eq(userId))))
                .or(q.id.in(JPAExpressions.select(r.questionId).from(r).where(r.userId.eq(userId))));
    }

    private BooleanExpression hasReviewStatus(Long userId, ReviewStatus reviewStatus) {
        if (reviewStatus == null) return null;
        return q.id.in(
                JPAExpressions.select(r.questionId)
                        .from(r)
                        .where(r.userId.eq(userId), r.status.eq(reviewStatus))
        );
    }

    private BooleanExpression hasAnswerFilter(Long userId, Boolean hasAnswer) {
        if (hasAnswer == null) return null;
        BooleanExpression answered = q.id.in(
                JPAExpressions.select(a.questionId).from(a).where(a.userId.eq(userId))
        );
        return Boolean.TRUE.equals(hasAnswer) ? answered : answered.not();
    }

    private BooleanExpression isMineFilter(Long userId, Boolean isMine) {
        if (!Boolean.TRUE.equals(isMine)) return null;
        return q.authorId.eq(userId);
    }

    private BooleanExpression isBookmarkedFilter(Long userId, Boolean isBookmarked) {
        if (!Boolean.TRUE.equals(isBookmarked)) return null;
        return isBookmarkedByUser(userId);
    }

    // ─── Sort helper ───────────────────────────────────────────────────────────

    @SuppressWarnings({"rawtypes", "unchecked"})
    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier[]{q.createdAt.desc()};
        }
        PathBuilder<Question> path = new PathBuilder<>(Question.class, "question");
        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                        path.get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }
}
