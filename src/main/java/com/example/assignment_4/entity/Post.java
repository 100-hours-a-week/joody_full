package com.example.assignment_4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    /** ✅ 게시글 제목 */
    @Column(nullable = false, length = 255)
    private String title;

    /** ✅ 게시글 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** ✅ 게시글 이미지 경로 */
    @Column(name = "post_image", length = 255)
    private String postImage;

    /** ✅ 좋아요 수 */
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    /** ✅ 조회수 */
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private int viewCount = 0;

    /** ✅ 기존 commentCount는 유지 (DB에 저장된 값) */
    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    /**
     * ✅ “보이는 댓글 수” (soft delete 제외 + 탈퇴 유저 제외)
     * - comments.deleted_at IS NULL
     * - users.deleted_at IS NULL
     */
    @Formula(
            "(SELECT COUNT(c.comment_id) " +
                    "   FROM comments c " +
                    "   JOIN users u ON u.user_id = c.user_id " +
                    "  WHERE c.post_id = post_id " +
                    "    AND c.deleted_at IS NULL " +
                    "    AND u.deleted_at IS NULL)"
    )
    private int visibleCommentCount;

    /** ✅ 생성일 (정렬용 커서로도 활용 가능) */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** ✅ 수정일 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** ✅ Soft Delete */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** ✅ 작성자 (User) — LAZY 로딩 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** ✅ 댓글 관계 (Cascade + orphanRemoval 적용) */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    /** ✅ 좋아요 관계 */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    /** ✅ Soft Delete 여부 확인 메서드 */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
