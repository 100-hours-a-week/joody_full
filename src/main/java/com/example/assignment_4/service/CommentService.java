package com.example.assignment_4.service;

import com.example.assignment_4.dto.CommentSummary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final Map<Long, List<CommentSummary>> comments = new HashMap<>();
    private long commentSequence = 1;
    private final PostService postService;

    public CommentService(PostService postService) {
        this.postService = postService;
    }

    public Long addComment(Long postId, String content) {
        var post = postService.findPost(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("invalid_request");
        }

        Long newCommentId = commentSequence++;
        CommentSummary newComment = new CommentSummary(
                newCommentId, "익명 사용자", content, LocalDateTime.now().toString()
        );

        comments.computeIfAbsent(postId, k -> new ArrayList<>()).add(newComment);
        post.setCommentCount(post.getCommentCount() + 1);

        return newCommentId;
    }

    public Map<String, Object> getComments(Long postId, int page, int size, String sortBy, String order, String keyword) {
        var post = postService.findPost(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        List<CommentSummary> postComments = comments.getOrDefault(postId, new ArrayList<>());

        // 검색
        if (keyword != null && !keyword.isBlank()) {
            postComments = postComments.stream()
                    .filter(c -> c.getContent().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // 정렬
        Comparator<CommentSummary> comparator = switch (sortBy) {
            case "author" -> Comparator.comparing(CommentSummary::getAuthor);
            case "content" -> Comparator.comparing(CommentSummary::getContent);
            default -> Comparator.comparing(CommentSummary::getCreatedAt);
        };
        if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();
        postComments = postComments.stream().sorted(comparator).toList();

        // 페이징
        int totalElements = postComments.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min((page - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<CommentSummary> pageContent = postComments.subList(fromIndex, toIndex);

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalElements", totalElements);
        result.put("content", pageContent);
        return result;
    }

    public void updateComment(Long postId, Long commentId, String newContent) {
        var postComments = comments.get(postId);
        if (postComments == null) throw new NoSuchElementException("comment_not_found");

        var target = postComments.stream()
                .filter(c -> c.getCommentId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("comment_not_found"));

        target.setContent(newContent);
    }

    public void deleteComment(Long postId, Long commentId) {
        var postComments = comments.get(postId);
        if (postComments == null) throw new NoSuchElementException("comment_not_found");

        boolean removed = postComments.removeIf(c -> c.getCommentId().equals(commentId));
        if (!removed) throw new NoSuchElementException("comment_not_found");

        postService.findPost(postId).ifPresent(p ->
                p.setCommentCount(Math.max(0, p.getCommentCount() - 1))
        );
    }
}
