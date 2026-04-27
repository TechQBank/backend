package com.qbank.group.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "question_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static QuestionGroup create(Long userId, String name, String description, boolean isPublic) {
        QuestionGroup group = new QuestionGroup();
        group.userId = userId;
        group.name = name;
        group.description = description;
        group.isPublic = isPublic;
        group.createdAt = LocalDateTime.now();
        return group;
    }

    public void update(String name, String description, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }
}
