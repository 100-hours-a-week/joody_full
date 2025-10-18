package com.example.assignment_4.service;

import com.example.assignment_4.dto.PostSummary;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LikeService {

    private final Map<Long, Set<Long>> postLikesByUser = new HashMap<>();
    private final PostService postService;

    public LikeService(PostService postService) {
        this.postService = postService;
    }

    public Map<String, Object> toggleLike(Long postId, Long userId) {
        var post = postService.findPost(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        postLikesByUser.putIfAbsent(postId, new HashSet<>());
        Set<Long> likedUsers = postLikesByUser.get(postId);

        boolean added;
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            post.setLikes(Math.max(0, post.getLikes() - 1));
            added = false;
        } else {
            likedUsers.add(userId);
            post.setLikes(post.getLikes() + 1);
            added = true;
        }

        return Map.of(
                "likes", post.getLikes(),
                "liked", added
        );
    }
}
