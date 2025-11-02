package com.example.assignment_4.service;

import com.example.assignment_4.dto.PostDetail;
import com.example.assignment_4.dto.PostSummary;
import com.example.assignment_4.entity.Post;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.PostRepository;
import com.example.assignment_4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 게시글 단일 조회 (LikeService, CommentService 등에서 사용)
     */
    public Optional<Post> findPost(Long postId) {
        return postRepository.findById(postId);
    }

    /**
     * ✅ 게시글 목록 조회 (검색 + 정렬 + 페이징)
     */
    public Map<String, Object> getPostList(int page, int size, String sort, String direction, String keyword) {
        List<Post> allPosts = postRepository.findAll();

        // 🔍 검색 필터
        List<Post> filtered = allPosts.stream()
                .filter(p -> keyword == null || keyword.isBlank()
                        || p.getTitle().contains(keyword)
                        || (p.getUser() != null && p.getUser().getNickname().contains(keyword)))
                .collect(Collectors.toList());

        // 🔢 정렬
        Comparator<Post> comparator = switch (sort) {
            case "views" -> Comparator.comparing(Post::getViewCount);
            case "likes" -> Comparator.comparing(Post::getLikeCount);
            case "createdAt" -> Comparator.comparing(Post::getCreatedAt);
            default -> Comparator.comparing(Post::getId);
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        filtered.sort(comparator);

        // 📄 페이징
        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min((page - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Post> paginated = filtered.subList(fromIndex, toIndex);

        // 📦 DTO 변환
        List<PostSummary> summaries = paginated.stream()
                .map(PostSummary::from)
                .collect(Collectors.toList());

        // 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalElements", totalElements);
        result.put("content", summaries);
        return result;
    }

    /**
     * ✅ 게시글 상세 조회 (+조회수 증가)
     */
    public PostDetail getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));
        post.setViewCount(post.getViewCount() + 1);
        return PostDetail.from(post);
    }

    /**
     * ✅ 게시글 작성
     */
    public Long createPost(Long userId, String title, String content, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user_not_found"));
        Post post = Post.builder()
                .title(title)
                .content(content)
                .postImage(imageUrl)
                .user(user)
                .build();
        postRepository.save(post);
        return post.getId();
    }

    /**
     * ✅ 게시글 수정
     */
    public void updatePost(Long postId, String title, String content, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));
        if (title.length() > 26) throw new IllegalArgumentException("invalid_request");
        post.setTitle(title);
        post.setContent(content);
        post.setPostImage(imageUrl);
        post.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * ✅ 게시글 삭제 (soft/hard)
     */
    public void deletePost(Long postId, boolean hard) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));
        if (hard) postRepository.delete(post);
        else post.setDeletedAt(LocalDateTime.now());
    }
}
