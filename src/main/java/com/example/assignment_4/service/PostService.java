package com.example.assignment_4.service;

import com.example.assignment_4.dto.CommentSummary;
import com.example.assignment_4.dto.PostDetail;
import com.example.assignment_4.dto.PostSummary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    // 인메모리 게시글 저장소 (DB 대체)
    private final List<PostSummary> posts = new ArrayList<>();
    private final Map<Long, List<CommentSummary>> comments = new HashMap<>();
    // 사용자별 좋아요 기록 저장용 (postId 기준)
    private final Map<Long, Set<Long>> postLikesByUser = new HashMap<>();
    private long commentSequence = 1;

    public PostService() {
        // 테스트용 데이터 생성
        for (int i = 1; i <= 50; i++) {
            posts.add(new PostSummary(
                    (long) i,
                    "게시글 제목 " + i,
                    i % 2 == 0 ? "joody" : "joo",
                    new Random().nextInt(500),
                    new Random().nextInt(100),
                    new Random().nextInt(20),
                    LocalDateTime.now().minusDays(i).toString(),
                    "테스트용 본문 내용입니다. 게시글 번호 " + i   // ✅ content 인자 추가
            ));
        }
    }

    public Map<String, Object> getPostList(
            int page, int size, String sort, String direction, String keyword
    ) {
        // 1. 검색
        List<PostSummary> filtered = posts.stream()
                .filter(p -> keyword == null || keyword.isBlank() ||
                        p.getTitle().contains(keyword) || p.getAuthor().contains(keyword))
                .collect(Collectors.toList());

        // 2. 정렬
        Comparator<PostSummary> comparator;
        switch (sort) {
            case "views" -> comparator = Comparator.comparing(PostSummary::getViews);
            case "likes" -> comparator = Comparator.comparing(PostSummary::getLikes);
            case "createdAt" -> comparator = Comparator.comparing(PostSummary::getCreatedAt);
            default -> comparator = Comparator.comparing(PostSummary::getPostId);
        }
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        filtered.sort(comparator);

        // 3. 페이징
        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int fromIndex = Math.min((page - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<PostSummary> content = filtered.subList(fromIndex, toIndex);

        // 4. 응답 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalElements", totalElements);
        result.put("content", content);

        return result;
    }

    public PostDetail getPostDetail(Long postId) {
        // 1. 게시글 찾기 (인메모리 리스트에서)
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            return null;
        }

        var post = postOpt.get();

        // 2. 조회수 증가
        post.setViews(post.getViews() + 1);

        // 3. 댓글 데이터 가져오기 (작성 API에서 저장한 Map 활용)
        List<CommentSummary> postComments = comments.getOrDefault(postId, new ArrayList<>());
        int commentCount = postComments.size();   // 댓글 수 계산

        // 4. 상세 DTO로 변환해서 반환
        return new PostDetail(
                post.getPostId(),
                post.getTitle(),
                post.getAuthor(),
                post.getCreatedAt(),
                post.getContent(),
                post.getViews(),
                post.getLikes(),
                commentCount,
                postComments
        );
    }


    /* 게시글 작성 */
    public Long createPost(String title, String content, String imageUrl) {
        // id는 간단하게 현재 리스트 크기 + 1로 처리 (DB라면 auto_increment)
        long newId = posts.size() + 1;

        PostSummary newPost = new PostSummary(
                newId,
                title,
                "joo",
                0,
                0,
                0,
                LocalDateTime.now().toString(),
                content
        );

        if (imageUrl != null) {
            newPost.setTitle(newPost.getTitle());
        }

        posts.add(newPost);
        return newId;
    }


    /* 게시글 수정 */

    public void updatePost(Long postId, String title, String content, String imageUrl) {
        // 게시글 찾기
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        if (title.length() > 26) {
            throw new IllegalArgumentException("invalid_request");
        }

        var post = postOpt.get();
        post.setTitle(title);
        post.setContent(content);

        if (imageUrl != null) {
            post.setTitle(post.getTitle());
        }
    }


    /* 게시글 삭제 */
    public void deletePost(Long postId, boolean hard) {
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        // hard=true면 실제 삭제, false면 임시 삭제라고 가정
        posts.remove(postOpt.get());
    }


    /*댓글 작성*/
    public Long addComment(Long postId, String content) {
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("invalid_request");
        }

        Long newCommentId = commentSequence++;

        CommentSummary newComment = new CommentSummary(
                newCommentId,
                "익명 사용자",   // 나중에 로그인 붙이면 작성자 정보 사용
                content,
                LocalDateTime.now().toString()
        );

        comments.computeIfAbsent(postId, k -> new ArrayList<>()).add(newComment);

        // 게시글 commentCount도 증가시켜주자 👇
        postOpt.get().setCommentCount(postOpt.get().getCommentCount() + 1);

        return newCommentId;
    }

    /*댓글 목록 조회*/
    public Map<String, Object> getComments(Long postId, int page, int size, String sortBy, String order, String keyword) {
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        List<CommentSummary> postComments = comments.getOrDefault(postId, new ArrayList<>());

        // 1. 검색 필터
        if (keyword != null && !keyword.isBlank()) {
            postComments = postComments.stream()
                    .filter(c -> c.getContent().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }

        // 2. 정렬
        Comparator<CommentSummary> comparator;
        switch (sortBy) {
            case "author" -> comparator = Comparator.comparing(CommentSummary::getAuthor);
            case "content" -> comparator = Comparator.comparing(CommentSummary::getContent);
            default -> comparator = Comparator.comparing(CommentSummary::getCreatedAt);
        }

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        postComments = postComments.stream()
                .sorted(comparator)
                .toList();

        // 3. 페이징
        int totalElements = postComments.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min((page - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<CommentSummary> pageContent = postComments.subList(fromIndex, toIndex);

        // 4. 응답 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalElements", totalElements);
        result.put("content", pageContent);

        return result;
    }


    /* 댓글 수정 */
    public void updateComment(Long postId, Long commentId, String newContent) {
        List<CommentSummary> postComments = comments.get(postId);

        if (postComments == null) {
            throw new NoSuchElementException("comment_not_found");
        }

        var target = postComments.stream()
                .filter(c -> c.getCommentId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("comment_not_found"));

        target.setContent(newContent);
    }

    /* 댓글 삭제 */

    public void deleteComment(Long postId, Long commentId) {
        List<CommentSummary> postComments = comments.get(postId);

        if (postComments == null) {
            throw new NoSuchElementException("comment_not_found");
        }

        boolean removed = postComments.removeIf(c -> c.getCommentId().equals(commentId));

        if (!removed) {
            throw new NoSuchElementException("comment_not_found");
        }

        // 댓글 삭제 시 해당 게시글의 댓글 수 감소
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        postOpt.ifPresent(p -> p.setCommentCount(Math.max(0, p.getCommentCount() - 1)));
    }


    /* 좋아요 추가 & 취소 */
    public int addLike(Long postId) {
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        var post = postOpt.get();
        post.setLikes(post.getLikes() + 1);
        return post.getLikes();
    }

    public int removeLike(Long postId) {
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        var post = postOpt.get();
        post.setLikes(Math.max(0, post.getLikes() - 1));
        return post.getLikes();
    }



    /*좋아요 추가 & 취소 토글*/
    public Map<String, Object> toggleLike(Long postId, Long userId) {
        var postOpt = posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst();

        if (postOpt.isEmpty()) {
            throw new NoSuchElementException("post_not_found");
        }

        var post = postOpt.get();

        // 게시글별 좋아요한 사용자 목록 가져오기
        postLikesByUser.putIfAbsent(postId, new HashSet<>());
        Set<Long> likedUsers = postLikesByUser.get(postId);

        boolean added;
        if (likedUsers.contains(userId)) {
            // 이미 좋아요 누른 상태 → 취소
            likedUsers.remove(userId);
            post.setLikes(Math.max(0, post.getLikes() - 1));
            added = false;
        } else {
            // 처음 누르는 경우 → 추가
            likedUsers.add(userId);
            post.setLikes(post.getLikes() + 1);
            added = true;
        }

        return Map.of(
                "likes", post.getLikes(),
                "liked", added   // true면 좋아요 추가됨, false면 취소됨
        );
    }





}
