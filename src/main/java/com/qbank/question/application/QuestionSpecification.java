package com.qbank.question.application;

import com.qbank.answer.domain.UserQuestionAnswer;
import com.qbank.bookmark.domain.Bookmark;
import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.question.domain.*;
import com.qbank.review.domain.ReviewStatus;
import com.qbank.review.domain.UserQuestionReview;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

public class QuestionSpecification {

    private QuestionSpecification() {}

    public static Specification<Question> publicSearch(QuestionSearchCondition condition) {
        return isPublic()
                .and(containsKeyword(condition.keyword()))
                .and(hasAnyTagId(condition.tagIds()))
                .and(hasCareerLevel(condition.careerLevel()))
                .and(hasDifficulty(condition.difficulty()));
    }

    public static Specification<Question> isPublic() {
        return (root, query, cb) ->
                cb.equal(root.get("visibility"), Visibility.PUBLIC);
    }

    public static Specification<Question> containsKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> {
            String like = "%" + keyword + "%";
            return cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("description"), like)
            );
        };
    }

    public static Specification<Question> hasCareerLevel(CareerLevel careerLevel) {
        if (careerLevel == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(root.get("careerLevel"), careerLevel);
    }

    public static Specification<Question> hasDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(root.get("difficulty"), difficulty);
    }

    public static Specification<Question> isAuthor(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("authorId"), authorId);
    }

    public static Specification<Question> hasVisibility(Visibility visibility) {
        if (visibility == null) return Specification.unrestricted();
        return (root, query, cb) -> cb.equal(root.get("visibility"), visibility);
    }

    public static Specification<Question> myQuestions(Long authorId, Visibility visibility) {
        return isAuthor(authorId).and(hasVisibility(visibility));
    }

    public static Specification<Question> isBookmarkedByUser(Long userId) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Bookmark> b = sub.from(Bookmark.class);
            sub.select(b.get("questionId"))
               .where(cb.equal(b.get("userId"), userId));
            return root.get("id").in(sub);
        };
    }

    // 내가 등록 | 북마크 | 답변 | 복습 설정한 질문
    public static Specification<Question> isStudyQuestion(Long userId) {
        return (root, query, cb) -> {
            Subquery<Long> bookmarkSub = query.subquery(Long.class);
            Root<Bookmark> b = bookmarkSub.from(Bookmark.class);
            bookmarkSub.select(b.get("questionId")).where(cb.equal(b.get("userId"), userId));

            Subquery<Long> answerSub = query.subquery(Long.class);
            Root<UserQuestionAnswer> a = answerSub.from(UserQuestionAnswer.class);
            answerSub.select(a.get("questionId")).where(cb.equal(a.get("userId"), userId));

            Subquery<Long> reviewSub = query.subquery(Long.class);
            Root<UserQuestionReview> r = reviewSub.from(UserQuestionReview.class);
            reviewSub.select(r.get("questionId")).where(cb.equal(r.get("userId"), userId));

            return cb.or(
                    cb.equal(root.get("authorId"), userId),
                    root.get("id").in(bookmarkSub),
                    root.get("id").in(answerSub),
                    root.get("id").in(reviewSub)
            );
        };
    }

    public static Specification<Question> hasReviewStatus(Long userId, ReviewStatus reviewStatus) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<UserQuestionReview> r = sub.from(UserQuestionReview.class);
            sub.select(r.get("questionId"))
               .where(cb.and(
                       cb.equal(r.get("userId"), userId),
                       cb.equal(r.get("status"), reviewStatus)
               ));
            return root.get("id").in(sub);
        };
    }

    public static Specification<Question> hasAnswerWritten(Long userId) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<UserQuestionAnswer> a = sub.from(UserQuestionAnswer.class);
            sub.select(a.get("questionId")).where(cb.equal(a.get("userId"), userId));
            return root.get("id").in(sub);
        };
    }

    public static Specification<Question> hasNoAnswer(Long userId) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<UserQuestionAnswer> a = sub.from(UserQuestionAnswer.class);
            sub.select(a.get("questionId")).where(cb.equal(a.get("userId"), userId));
            return cb.not(root.get("id").in(sub));
        };
    }

    public static Specification<Question> hasAnyTagId(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<QuestionTag> qt = sub.from(QuestionTag.class);

            sub.select(qt.get("question").get("id"))
                    .where(qt.get("tag").get("id").in(tagIds));

            return root.get("id").in(sub);
        };
    }
}