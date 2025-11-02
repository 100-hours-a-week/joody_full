package com.example.assignment_4.service;

import com.example.assignment_4.entity.Like;
import com.example.assignment_4.entity.Post;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.LikeRepository;
import com.example.assignment_4.repository.PostRepository;
import com.example.assignment_4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Map<String, Object> toggleLike(Long postId, Long userId) {

        // 1) 게시글 찾기 (삭제된 글이면 에러)
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        // 2) 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user_not_found"));

        // 3) 이미 눌렀는지 확인
        var existing = likeRepository.findByPost_IdAndUser_Id(postId, userId);

        boolean liked;
        if (existing.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existing.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            liked = false;
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            liked = true;
        }

        return Map.of(
                "post_id", postId,
                "like_count", post.getLikeCount(),
                "liked", liked
        );
    }
}
