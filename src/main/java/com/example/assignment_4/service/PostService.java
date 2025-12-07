package com.example.assignment_4.service;

import com.example.assignment_4.dto.PostDetail;
import com.example.assignment_4.dto.PostSummary;
import com.example.assignment_4.entity.Post;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.CommentRepository;
import com.example.assignment_4.repository.PostRepository;
import com.example.assignment_4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Optional<Post> findByPostAndDeletedFalse(Long postId) {
        return postRepository.findById(postId);
    }

    /**
     * ✅ 게시글 목록 조회 (커서 기반 인피니티 스크롤)
     */
    public Map<String, Object> getPostList(LocalDateTime cursorCreatedAt, int size, String keyword) {
        // 1️⃣ DB에서 커서 기준 다음 게시글 가져오기
        Pageable pageable = PageRequest.of(0, size);
        List<Post> posts = postRepository.findNextPosts(cursorCreatedAt, keyword, pageable);

        // 2️⃣ 검색어 필터링 (옵션)
//        if (keyword != null && !keyword.isBlank()) {
//            posts = posts.stream()
//                    .filter(p -> p.getTitle().contains(keyword)
//                            || (p.getUser() != null && p.getUser().getNickname().contains(keyword)))
//                    .collect(Collectors.toList());
//        }

        // 3️⃣ DTO 변환
        List<PostSummary> summaries = posts.stream()
                .map(post -> {
                    try {
                        return PostSummary.from(post);
                    } catch (Exception e) {
                        return PostSummary.fromDeletedUser(post);
                    }
                })
                .collect(Collectors.toList());

        // 4️⃣ 다음 커서 (마지막 게시글의 createdAt)
        LocalDateTime nextCursor = summaries.isEmpty()
                ? null
                : summaries.get(summaries.size() - 1).getCreatedAt();

        // 5️⃣ 다음 페이지 존재 여부
        boolean hasNext = posts.size() == size;

        // 6️⃣ 응답 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("content", summaries);
        result.put("nextCursor", nextCursor);
        result.put("hasNext", hasNext);

        return result;
    }


    /**
     * ✅ 게시글 상세 조회 (+조회수 증가)
     */
    public PostDetail getPostDetail(Long postId) {
        Post post = postRepository.findPostWithUser(postId)
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
    public Long updatePost(Long postId, String title, String content, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post_not_found"));

        if (title.length() > 26) throw new IllegalArgumentException("invalid_request");

        post.setTitle(title);
        post.setContent(content);

        // ✅ 새 이미지가 있을 경우에만 변경
        if (imageUrl != null && !imageUrl.isBlank()) {
            post.setPostImage(imageUrl);
        }

        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        // ✅ 수정된 게시글 ID 반환
        return post.getId();
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
