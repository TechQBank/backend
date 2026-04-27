package com.qbank.follow.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "follows")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long followerId;

    @Column(nullable = false)
    private Long followeeId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static Follow of(Long followerId, Long followeeId) {
        Follow follow = new Follow();
        follow.followerId = followerId;
        follow.followeeId = followeeId;
        follow.createdAt = LocalDateTime.now();
        return follow;
    }
}
