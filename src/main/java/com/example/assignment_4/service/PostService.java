package com.example.assignment_4.service;

import com.example.assignment_4.dto.PostDetail;
import com.example.assignment_4.dto.PostSummary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final List<PostSummary> posts = new ArrayList<>();
    private long postSequence = 1L;

    public PostService() {
        // 테스트용 초기 데이터
        for (int i = 1; i <= 50; i++) {
            posts.add(new PostSummary(
                    postSequence++,
                    "게시글 제목 " + i,
                    i % 2 == 0 ? "joody" : "joo",
                    new Random().nextInt(500),
                    new Random().nextInt(100),
                    new Random().nextInt(20),
                    LocalDateTime.now().minusDays(i).toString(),
                    "테스트용 본문 내용입니다. 게시글 번호 " + i
            ));
        }
    }

    public Map<String, Object> getPostList(int page, int size, String sort, String direction, String keyword) {
        // 검색
        List<PostSummary> filtered = posts.stream()
                .filter(p -> keyword == null || keyword.isBlank()
                        || p.getTitle().contains(keyword) || p.getAuthor().contains(keyword))
                .collect(Collectors.toList());

        // 정렬
        Comparator<PostSummary> comparator = switch (sort) {
            case "views" -> Comparator.comparing(PostSummary::getViews);
            case "likes" -> Comparator.comparing(PostSummary::getLikes);
            case "createdAt" -> Comparator.comparing(PostSummary::getCreatedAt);
            default -> Comparator.comparing(PostSummary::getPostId);
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        filtered.sort(comparator);

        // 페이징
        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min((page - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<PostSummary> content = filtered.subList(fromIndex, toIndex);

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalElements", totalElements);
        result.put("content", content);

        return result;
    }

    public PostDetail getPostDetail(Long postId) {
        var postOpt = posts.stream().filter(p -> p.getPostId().equals(postId)).findFirst();
        if (postOpt.isEmpty()) return null;

        var post = postOpt.get();
        post.setViews(post.getViews() + 1);

        return new PostDetail(
                post.getPostId(),
                post.getTitle(),
                post.getAuthor(),
                post.getCreatedAt(),
                post.getContent(),
                post.getViews(),
                post.getLikes(),
                post.getCommentCount(),
                Collections.emptyList()  // 댓글은 CommentService에서 따로 조회
        );
    }

    public Long createPost(String title, String content, String imageUrl) {
        long newId = postSequence++;
        PostSummary newPost = new PostSummary(
                newId, title, "joo", 0, 0, 0,
                LocalDateTime.now().toString(), content
        );
        posts.add(newPost);
        return newId;
    }

    public void updatePost(Long postId, String title, String content, String imageUrl) {
        var post = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        if (title.length() > 26) throw new IllegalArgumentException("invalid_request");

        post.setTitle(title);
        post.setContent(content);
    }

    public void deletePost(Long postId, boolean hard) {
        posts.removeIf(p -> p.getPostId().equals(postId));
    }

    // 댓글 수 업데이트를 위해 CommentService에서 접근
    public Optional<PostSummary> findPost(Long postId) {
        return posts.stream().filter(p -> p.getPostId().equals(postId)).findFirst();
    }
}
