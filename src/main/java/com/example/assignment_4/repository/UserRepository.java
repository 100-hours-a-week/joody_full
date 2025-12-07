package com.example.assignment_4.repository;

import com.example.assignment_4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 유저 찾기 (로그인 시 사용)
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    // 이메일 중복 체크 (회원가입 시 사용)
    boolean existsByEmail(String email);

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);
}
