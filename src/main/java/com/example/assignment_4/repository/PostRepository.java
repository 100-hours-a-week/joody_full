package com.example.assignment_4.repository;

import com.example.assignment_4.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /** ✅ 단일 게시글 조회 (삭제된 글 제외) */
    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    /**
     * ✅ 커서 기반 게시글 조회 (createdAt 기준)
     * - 최신 게시글부터 조회
     * - 삭제된 게시글 및 탈퇴 유저 제외
     */
    @Query("""
    SELECT p
    FROM Post p
    JOIN FETCH p.user u
    WHERE p.deletedAt IS NULL
      AND u.deletedAt IS NULL
      AND (:cursorCreatedAt IS NULL OR p.createdAt < :cursorCreatedAt)
      AND (:keyword IS NULL OR p.title LIKE %:keyword%
                       OR p.content LIKE %:keyword%
                       OR u.nickname LIKE %:keyword%)
    ORDER BY p.createdAt DESC
""")
    List<Post> findNextPosts(@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
                             @Param("keyword") String keyword,
                             Pageable pageable);


    @Query("""
    SELECT p
    FROM Post p
    JOIN FETCH p.user u
    WHERE p.id = :postId
      AND p.deletedAt IS NULL
      AND u.deletedAt IS NULL
""")
    Optional<Post> findPostWithUser(@Param("postId") Long postId);

}
