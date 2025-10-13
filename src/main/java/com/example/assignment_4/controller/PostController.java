package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.CommentCreateRequest;
import com.example.assignment_4.dto.CommentUpdateRequest;
import com.example.assignment_4.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /* 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ) {
        var data = postService.getPostList(page, size, sort, direction, keyword);
        return ResponseEntity.ok(new ApiResponse<>("read_success", data));
    }

    /* 게시글 상세 조회 */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getPostDetail(@PathVariable Long postId) {
        var detail = postService.getPostDetail(postId);
        return ResponseEntity.ok(new ApiResponse<>("read_success", detail));
    }

    /* 게시글 작성 */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        if (title.length() > 26) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("invalid_request", null));
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String uploadDir = "uploads";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, image.getBytes());

            imageUrl = "http://localhost:8080/uploads/" + filename;
        }

        Long postId = postService.createPost(title, content, imageUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("create_success", Map.of("post_id", postId)));
    }

    /* 게시글 수정 */
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> updatePost(
            @PathVariable Long postId,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String uploadDir = "uploads";
            Files.createDirectories(Paths.get(uploadDir));
            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, image.getBytes());
            imageUrl = "http://localhost:8080/uploads/" + filename;
        }

        postService.updatePost(postId, title, content, imageUrl);
        return ResponseEntity.ok(new ApiResponse<>("update_success", Map.of("post_id", postId)));
    }

    /* 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(
            @PathVariable Long postId,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        boolean hard = body != null && body.get("hard") != null && (Boolean) body.get("hard");
        postService.deletePost(postId, hard);
        return ResponseEntity.ok(new ApiResponse<>("delete_success", null));
    }

    /* 댓글 작성 */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Long commentId = postService.addComment(postId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("comment_created", Map.of("comment_id", commentId)));
    }

    /* 댓글 목록 조회 */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> getCommentList(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String keyword
    ) {
        var data = postService.getComments(postId, page, size, sortBy, order, keyword);
        return ResponseEntity.ok(new ApiResponse<>("read_success", data));
    }

    /* 댓글 수정 */
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        postService.updateComment(postId, commentId, request.getContent());
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }

    /* 댓글 삭제 */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postService.deleteComment(postId, commentId);
        return ResponseEntity.ok(new ApiResponse<>("comment_deleted", null));
    }

    /* 좋아요 토글 */
    @PostMapping("/{postId}/likes/toggle")
    public ResponseEntity<ApiResponse<?>> toggleLike(@PathVariable Long postId) {
        Long userId = 1L; // TODO: 실제 로그인 유저 ID로 교체
        Map<String, Object> result = postService.toggleLike(postId, userId);
        String message = (boolean) result.get("liked") ? "like_added" : "like_removed";
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }
}
