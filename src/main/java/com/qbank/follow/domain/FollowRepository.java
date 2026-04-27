package com.qbank.follow.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    long countByFolloweeId(Long followeeId);
    long countByFollowerId(Long followerId);
    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
