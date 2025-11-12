//package com.example.assignment_4.controller;
//
//import com.example.assignment_4.common.ApiResponse;
//import com.example.assignment_4.dto.CommentCreateRequest;
//import com.example.assignment_4.dto.CommentUpdateRequest;
//import com.example.assignment_4.dto.PostRequest;
//import com.example.assignment_4.service.CommentService;
//import com.example.assignment_4.service.FileService;
//import com.example.assignment_4.service.LikeService;
//import com.example.assignment_4.service.PostService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
//import java.io.IOException;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/posts")
//@Tag(name = "Post API", description = "게시글, 댓글, 좋아요 관련 API")
//public class PostController {
//
//    private final PostService postService;
//    private final CommentService commentService;
//    private final LikeService likeService;
//    private final FileService fileService;
//
//    @Operation(summary = "게시글 목록 조회", description = "페이지네이션, 정렬, 검색을 포함한 게시글 목록을 조회합니다.")
//    @GetMapping
//    public ResponseEntity<ApiResponse<?>> getPostList(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sort,
//            @RequestParam(defaultValue = "desc") String direction,
//            @RequestParam(required = false) String keyword
//    ) {
//        var data = postService.getPostList(page, size, sort, direction, keyword);
//        return ResponseEntity.ok(new ApiResponse<>("read_success", data));
//    }
//
//    @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 기반으로 상세 정보를 조회합니다.")
//    @GetMapping("/{postId}")
//    public ResponseEntity<ApiResponse<?>> getPostDetail(@PathVariable Long postId) {
//        var detail = postService.getPostDetail(postId);
//        if (detail == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse<>("post_not_found", null));
//        }
//        return ResponseEntity.ok(new ApiResponse<>("read_success", detail));
//    }
//
//    @Operation(summary = "게시글 작성", description = "제목, 내용, 이미지를 포함하여 새 게시글을 작성합니다.")
//    @PostMapping(consumes = "multipart/form-data")
//    public ResponseEntity<ApiResponse<?>> createPost(
//            @Valid @ModelAttribute PostRequest request
//    ) throws IOException {
//        String imageUrl = fileService.uploadFile(request.getImage());
//        Long postId = postService.createPost(request.getTitle(), request.getContent(), imageUrl);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ApiResponse<>("create_success", Map.of("post_id", postId)));
//    }
//
//    @Operation(summary = "게시글 수정", description = "게시글 ID를 기반으로 제목, 내용, 이미지를 수정합니다.")
//    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
//    public ResponseEntity<ApiResponse<?>> updatePost(
//            @PathVariable Long postId,
//            @Valid @ModelAttribute PostRequest request
//    ) throws IOException {
//        String imageUrl = fileService.uploadFile(request.getImage());
//        postService.updatePost(postId, request.getTitle(), request.getContent(), imageUrl);
//        return ResponseEntity.ok(new ApiResponse<>("update_success", Map.of("post_id", postId)));
//    }
//
//    @Operation(summary = "게시글 삭제", description = "게시글 ID를 기반으로 게시글을 삭제합니다. (hard 옵션 사용 시 영구 삭제)")
//    @DeleteMapping("/{postId}")
//    public ResponseEntity<ApiResponse<?>> deletePost(
//            @PathVariable Long postId,
//            @RequestBody(required = false) Map<String, Object> body
//    ) {
//        boolean hard = body != null && body.get("hard") != null && (Boolean) body.get("hard");
//        postService.deletePost(postId, hard);
//        return ResponseEntity.ok(new ApiResponse<>("delete_success", null));
//    }
//
//    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다.")
//    @PostMapping("/{postId}/comments")
//    public ResponseEntity<ApiResponse<?>> createComment(
//            @PathVariable Long postId,
//            @Valid @RequestBody CommentCreateRequest request
//    ) {
//        Long commentId = commentService.addComment(postId, request.getContent());
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ApiResponse<>("comment_created", Map.of("comment_id", commentId)));
//    }
//
//    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
//    @GetMapping("/{postId}/comments")
//    public ResponseEntity<ApiResponse<?>> getCommentList(
//            @PathVariable Long postId,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String order,
//            @RequestParam(required = false) String keyword
//    ) {
//        var data = commentService.getComments(postId, page, size, sortBy, order, keyword);
//        return ResponseEntity.ok(new ApiResponse<>("read_success", data));
//    }
//
//    @Operation(summary = "댓글 수정", description = "특정 게시글의 댓글 내용을 수정합니다.")
//    @PutMapping("/{postId}/comments/{commentId}")
//    public ResponseEntity<ApiResponse<?>> updateComment(
//            @PathVariable Long postId,
//            @PathVariable Long commentId,
//            @Valid @RequestBody CommentUpdateRequest request
//    ) {
//        commentService.updateComment(postId, commentId, request.getContent());
//        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
//    }
//
//    @Operation(summary = "댓글 삭제", description = "특정 게시글의 댓글을 삭제합니다.")
//    @DeleteMapping("/{postId}/comments/{commentId}")
//    public ResponseEntity<ApiResponse<?>> deleteComment(
//            @PathVariable Long postId,
//            @PathVariable Long commentId
//    ) {
//        commentService.deleteComment(postId, commentId);
//        return ResponseEntity.ok(new ApiResponse<>("comment_deleted", null));
//    }
//
//    @Operation(summary = "좋아요 토글", description = "특정 게시글에 대한 좋아요를 추가하거나 취소합니다.")
//    @PostMapping("/{postId}/likes/toggle")
//    public ResponseEntity<ApiResponse<?>> toggleLike(@PathVariable Long postId) {
//        Long userId = 1L; // TODO: 로그인 연동
//        Map<String, Object> result = likeService.toggleLike(postId, userId);
//        String message = (boolean) result.get("liked") ? "like_added" : "like_removed";
//        return ResponseEntity.ok(new ApiResponse<>(message, result));
//    }
//}

package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.CommentCreateRequest;
import com.example.assignment_4.dto.CommentUpdateRequest;
import com.example.assignment_4.dto.PostRequest;
import com.example.assignment_4.service.CommentService;
import com.example.assignment_4.service.FileService;
import com.example.assignment_4.service.LikeService;
import com.example.assignment_4.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Tag(name = "Post API", description = "게시글, 댓글, 좋아요 관련 API (JPA 기반)")
public class PostController {

    private final PostService postService;
    private final FileService fileService;
    private final LikeService likeService;
    private final CommentService commentService;

    /** ✅ 게시글 목록 조회 (커서 기반 인피니티 스크롤) */
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "커서 기반 인피니티 스크롤 형태로 게시글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<?>> getPostList(
            @RequestParam(required = false) String cursorCreatedAt,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        // 🔹 String → LocalDateTime 변환 (직접 처리)
        LocalDateTime cursor = null;
        try {
            if (cursorCreatedAt != null && !cursorCreatedAt.isBlank()) {
                cursor = LocalDateTime.parse(cursorCreatedAt); // "2025-11-08T12:30:00"
            }
        } catch (Exception e) {
            // 변환 실패 시 null로 처리 (첫 페이지로 인식)
            cursor = null;
        }

        var data = postService.getPostList(cursor, size, keyword);
        return ResponseEntity.ok(new ApiResponse<>("post_list_success", data));
    }

    /** ✅ 게시글 상세 조회 */
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 기반으로 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getPostDetail(@PathVariable Long postId) {
        var detail = postService.getPostDetail(postId);
        return ResponseEntity.ok(new ApiResponse<>("read_success", detail));
    }

    /** ✅ 게시글 작성 */
    @Operation(summary = "게시글 작성", description = "특정 유저가 제목, 내용, 이미지를 포함하여 새 게시글을 작성합니다.")
    @PostMapping(value = "/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createPost(
            @PathVariable Long userId,
            @Valid @ModelAttribute PostRequest request
    ) throws IOException {
        String imageUrl = fileService.uploadFile(request.getImage());
        Long postId = postService.createPost(userId, request.getTitle(), request.getContent(), imageUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("create_success", Map.of("post_id", postId)));
    }

    /** ✅ 게시글 수정 */
    @Operation(summary = "게시글 수정", description = "게시글 ID를 기반으로 제목, 내용, 이미지를 수정합니다.")
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> updatePost(
            @PathVariable Long postId,
            @Valid @ModelAttribute PostRequest request
    ) throws IOException {
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = fileService.uploadFile(request.getImage());
        }

        Long updatedPostId = postService.updatePost(postId, request.getTitle(), request.getContent(), imageUrl);
        return ResponseEntity.ok(new ApiResponse<>("update_success", Map.of("post_id", updatedPostId)));
    }

    /** ✅ 게시글 삭제 */
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 기반으로 게시글을 삭제합니다. (hard 옵션 사용 시 영구 삭제)")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "false") boolean hard
    ) {
        postService.deletePost(postId, hard);
        return ResponseEntity.ok(new ApiResponse<>("delete_success", null));
    }

    /** ✅ 댓글 작성 */
    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다.")
    @PostMapping("/{postId}/comments/{userId}")
    public ResponseEntity<ApiResponse<?>> createComment(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Long commentId = commentService.addComment(postId, userId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("comment_created", Map.of("comment_id", commentId)));
    }

    /** ✅ 댓글 목록 조회 */
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> getCommentList(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String keyword
    ) {
        var data = commentService.getComments(postId, page, size, sortBy, order, keyword);
        return ResponseEntity.ok(new ApiResponse<>("read_success", data));
    }

    /** ✅ 댓글 수정 */
    @Operation(summary = "댓글 수정", description = "특정 게시글의 댓글 내용을 수정합니다.")
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }

    /** ✅ 댓글 삭제 */
    @Operation(summary = "댓글 삭제", description = "특정 게시글의 댓글을 삭제합니다.")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @PathVariable Long postId,     // 사용하지 않더라도 받아야 함
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>("comment_deleted", null));
    }

    /** ✅ 좋아요 토글 */
    @Operation(summary = "좋아요 토글", description = "특정 게시글에 대한 좋아요를 추가하거나 취소합니다.")
    @PostMapping("/{postId}/likes/toggle")
    public ResponseEntity<ApiResponse<?>> toggleLike(
            @PathVariable Long postId,
            @RequestParam Long userId   //이걸 꼭 받아야 Postman에서 테스트 가능해
    ) {
        var result = likeService.toggleLike(postId, userId);
        String message = (boolean) result.get("liked") ? "like_added" : "like_removed";
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }
}
