package com.example.assignment_4.repository;

import com.example.assignment_4.entity.Comment;
import com.example.assignment_4.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글에 달린 모든 댓글 조회
    List<Comment> findByPost(Post post);
}
