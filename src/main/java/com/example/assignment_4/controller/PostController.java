package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.CommentCreateRequest;
import com.example.assignment_4.dto.CommentUpdateRequest;
import com.example.assignment_4.dto.PostRequest;
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
import java.util.NoSuchElementException;

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
        try {
            var data = postService.getPostList(page, size, sort, direction, keyword);
            return ResponseEntity.ok(new ApiResponse<>("read_success", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getPostDetail(@PathVariable("postId") Long postId) {
        try {
            var detail = postService.getPostDetail(postId);
            if (detail == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("post_not_found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>("read_success", detail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    /*게시글 작성*/
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            // ✅ 제목 길이 체크 (27자 이상 에러)
            if (title.length() > 26) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>("invalid_request", null));
            }

            // ✅ 이미지 저장 처리 (선택)
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                String uploadDir = "uploads";
                Files.createDirectories(Paths.get(uploadDir));

                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);
                Files.write(filePath, image.getBytes());

                imageUrl = "http://localhost:8080/uploads/" + filename;
            }

            // ✅ Service 호출
            Long postId = postService.createPost(title, content, imageUrl);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("create_success", Map.of("post_id", postId)));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    /*게시글 수정*/
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> updatePost(
            @PathVariable Long postId,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
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

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    /*게시글 삭제*/
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> body
    ) {
        try {
            boolean hard = body.get("hard") != null && (Boolean) body.get("hard");
            postService.deletePost(postId, hard);
            return ResponseEntity.ok(new ApiResponse<>("delete_success", null));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    /*댓글 작성*/
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        try {
            Long commentId = postService.addComment(postId, request.getContent());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("comment_created", Map.of("comment_id", commentId)));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    /*댓글 목록 조회*/
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> getCommentList(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String keyword
    ) {
        try {
            var data = postService.getComments(postId, page, size, sortBy, order, keyword);
            return ResponseEntity.ok(new ApiResponse<>("read_success", data));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /*댓글 수정*/
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        try {
            postService.updateComment(postId, commentId, request.getContent());
            return ResponseEntity.ok(new ApiResponse<>("update_success", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("comment_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /*댓글 삭제*/
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        try {
            postService.deleteComment(postId, commentId);
            return ResponseEntity.ok(new ApiResponse<>("comment_deleted", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("comment_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /*좋아요 추가 & 취소*/
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<?>> addLike(@PathVariable Long postId) {
        try {
            int likes = postService.addLike(postId);
            return ResponseEntity.ok(new ApiResponse<>("like_added", Map.of("likes", likes)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<?>> removeLike(@PathVariable Long postId) {
        try {
            int likes = postService.removeLike(postId);
            return ResponseEntity.ok(new ApiResponse<>("like_removed", Map.of("likes", likes)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    /*좋아요 수 추가 & 취소 토글*/
    @PostMapping("/{postId}/likes/toggle")
    public ResponseEntity<ApiResponse<?>> toggleLike(@PathVariable Long postId) {
        try {
            // 실제로는 로그인된 사용자 ID를 가져와야 함 (여기서는 1L로 고정)
            Long userId = 1L;
            Map<String, Object> result = postService.toggleLike(postId, userId);

            if ((boolean) result.get("liked")) {
                return ResponseEntity.ok(new ApiResponse<>("like_added", result));
            } else {
                return ResponseEntity.ok(new ApiResponse<>("like_removed", result));
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }



}
