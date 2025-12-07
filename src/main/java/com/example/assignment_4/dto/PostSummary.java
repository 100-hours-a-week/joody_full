package com.example.assignment_4.dto;

import com.example.assignment_4.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummary {

    private Long id;
    private String title;
    private String author;
    private String authorProfileImage;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostSummary from(Post post) {
        return PostSummary.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getUser() != null ? post.getUser().getNickname() : "(탈퇴한 사용자)")
                .authorProfileImage(post.getUser() != null ? post.getUser().getProfileImage() : null)
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .commentCount(post.getVisibleCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }

    /** ✅ 유저 정보가 없는(탈퇴한) 게시글용 fallback 변환 */
    public static PostSummary fromDeletedUser(Post post) {
        return PostSummary.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author("(탈퇴한 사용자)") // ✅ 안전하게 null 처리
                .authorProfileImage(null)  // ✅ 탈퇴한 사람은 null
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .commentCount(post.getVisibleCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
