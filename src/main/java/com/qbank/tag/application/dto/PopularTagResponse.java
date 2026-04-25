package com.qbank.tag.application.dto;

import com.qbank.tag.domain.TagPopularProjection;

public record PopularTagResponse(Long id, String name, long questionCount) {

    public static PopularTagResponse from(TagPopularProjection p) {
        return new PopularTagResponse(p.getId(), p.getName(), p.getQuestionCount());
    }
}
