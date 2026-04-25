package com.qbank.bookmark.domain;

import com.qbank.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bookmarks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    public static Bookmark of(Long userId, Long questionId) {
        Bookmark bookmark = new Bookmark();
        bookmark.userId = userId;
        bookmark.questionId = questionId;
        return bookmark;
    }
}
