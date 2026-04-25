package com.qbank.question.application;

import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.question.domain.*;
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