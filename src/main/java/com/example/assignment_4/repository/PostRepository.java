package com.example.assignment_4.repository;

import com.example.assignment_4.entity.Post;
import com.example.assignment_4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 유저가 작성한 게시글 조회
    List<Post> findByUser(User user);

    // 제목 검색 기능 (키워드 기반)
    List<Post> findByTitleContaining(String keyword);

    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

}
