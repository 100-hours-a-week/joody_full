package com.example.assignment_4.service;

import com.example.assignment_4.dto.CommentSummary;
import com.example.assignment_4.entity.Comment;
import com.example.assignment_4.entity.Post;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 댓글 작성
     */
    public Long addComment(Long postId, Long userId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("invalid_request");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user_not_found"));

        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        // 댓글 수 증가
        post.setCommentCount(post.getCommentCount() + 1);

        return comment.getId();
    }

    /**
     * ✅ 댓글 목록 조회 (검색 + 정렬 + 페이징)
     */
    public Map<String, Object> getComments(Long postId, int page, int size, String sortBy, String order, String keyword) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        List<Comment> allComments = commentRepository.findByPost(post);

        // 🔍 검색
        if (keyword != null && !keyword.isBlank()) {
            allComments = allComments.stream()
                    .filter(c -> c.getContent().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // 🔢 정렬
        Comparator<Comment> comparator = switch (sortBy) {
            case "author" -> Comparator.comparing(c -> c.getUser().getNickname());
            case "content" -> Comparator.comparing(Comment::getContent);
            default -> Comparator.comparing(Comment::getCreatedAt);
        };
        if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();
        allComments.sort(comparator);

        // 📄 페이징
        int totalElements = allComments.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min((page - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Comment> pageContent = allComments.subList(fromIndex, toIndex);

        // DTO 변환
        List<CommentSummary> summaries = pageContent.stream()
                .map(CommentSummary::from)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalElements", totalElements);
        result.put("content", summaries);
        return result;
    }

    /**
     * ✅ 댓글 수정
     */
    public void updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("comment_not_found"));

        if (newContent == null || newContent.isBlank())
            throw new IllegalArgumentException("invalid_request");

        comment.setContent(newContent);
        comment.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * ✅ 댓글 삭제 (soft delete)
     */
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("comment_not_found"));

        comment.setDeletedAt(LocalDateTime.now());

        // 댓글 수 감소
        Post post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
    }
}
