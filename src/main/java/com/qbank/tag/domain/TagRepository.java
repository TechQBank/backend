package com.qbank.tag.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);
    List<Tag> findAllByOrderByNameAsc();
    List<Tag> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
            SELECT t.id AS id, t.name AS name, COUNT(qt.id) AS questionCount
            FROM QuestionTag qt
            JOIN qt.tag t
            JOIN qt.question q
            WHERE q.visibility = 'PUBLIC'
            GROUP BY t.id, t.name
            ORDER BY COUNT(qt.id) DESC
            """)
    List<TagPopularProjection> findPopularTags(Pageable pageable);
}
