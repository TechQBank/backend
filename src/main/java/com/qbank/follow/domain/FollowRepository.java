package com.qbank.follow.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    long countByFolloweeId(Long followeeId);
    long countByFollowerId(Long followerId);
    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    List<Follow> findByFolloweeId(Long followeeId);
    List<Follow> findByFollowerId(Long followerId);

    @Query("SELECT f.followeeId AS userId, COUNT(f) AS count FROM Follow f WHERE f.followeeId IN :userIds GROUP BY f.followeeId")
    List<FollowCountProjection> countByFolloweeIdIn(@Param("userIds") List<Long> userIds);

    @Query("SELECT f.followerId AS userId, COUNT(f) AS count FROM Follow f WHERE f.followerId IN :userIds GROUP BY f.followerId")
    List<FollowCountProjection> countByFollowerIdIn(@Param("userIds") List<Long> userIds);

    @Query("SELECT f.followeeId FROM Follow f WHERE f.followerId = :followerId AND f.followeeId IN :followeeIds")
    List<Long> findFolloweeIdsByFollowerIdAndFolloweeIdIn(@Param("followerId") Long followerId, @Param("followeeIds") List<Long> followeeIds);

    @Modifying
    @Query(value = "INSERT IGNORE INTO follows (follower_id, followee_id, created_at) VALUES (:followerId, :followeeId, NOW())", nativeQuery = true)
    void insertIgnore(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
}
