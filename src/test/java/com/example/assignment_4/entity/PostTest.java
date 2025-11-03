package com.example.assignment_4.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class PostTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("✅ Post 엔티티가 정상적으로 저장된다")
    void savePostEntityTest() {
        // given
        User user = createUniqueUser("writer");
        Post post = Post.builder()
                .title("첫 번째 게시글")
                .content("이것은 테스트 게시글입니다.")
                .postImage("post1.png")
                .user(user)
                .build();

        // when
        em.persist(post);
        em.flush();
        em.clear();

//        // then
//        Post foundPost = em.find(Post.class, post.getId());
//        assertThat(foundPost).isNotNull();
//        assertThat(foundPost.getTitle()).isEqualTo("첫 번째 게시글");
//        assertThat(foundPost.getUser().getEmail()).contains("writer");
//
//        System.out.println("✅ 저장된 Post ID: " + foundPost.getId());
//        System.out.println("✅ 작성자: " + foundPost.getUser().getNickname());
//        System.out.println("✅ createdAt: " + foundPost.getCreatedAt());

        Post foundPost = em.find(Post.class, post.getId());
        assertThat(foundPost).isNotNull();
    }

    @Test
    @DisplayName("✅ Post 수정 시 UPDATE 쿼리가 실행되고 updatedAt이 자동 갱신된다")
    void updatePostEntityTest() throws InterruptedException {
        // given - 먼저 게시글 저장
        User user = createUniqueUser("editor");
        Post post = Post.builder()
                .title("수정 전 제목")
                .content("수정 전 내용")
                .user(user)
                .build();

        em.persist(post);  // INSERT 발생
        em.flush();
        em.clear();        // 1차 캐시 비우기 → 실제 DB에서 다시 조회

        // when - 기존 엔티티 조회 후 수정
        Post foundPost = em.find(Post.class, post.getId()); // 영속 상태로 관리됨
        LocalDateTime beforeUpdate = foundPost.getUpdatedAt();

        Thread.sleep(1000); // updatedAt 차이 확인용 (1초 대기)

        // ✅ 수정만 하고 persist() 호출 X
        foundPost.setTitle("수정된 제목");
        foundPost.setContent("수정된 내용");
        foundPost.setLikeCount(10);
        foundPost.setViewCount(100);

        em.flush();  // 변경 감지 → UPDATE 쿼리 실행
        em.clear();

        // then
        Post updatedPost = em.find(Post.class, post.getId());
        assertThat(updatedPost.getUpdatedAt()).isAfter(beforeUpdate);
        assertThat(updatedPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.getLikeCount()).isEqualTo(10);
        assertThat(updatedPost.getViewCount()).isEqualTo(100);

        System.out.println("✅ updatedAt 변경 전: " + beforeUpdate);
        System.out.println("✅ updatedAt 변경 후: " + updatedPost.getUpdatedAt());
    }

    @Test
    @DisplayName("✅ 모든 게시글을 조회할 수 있다")
    void findAllPostsTest() {
        // given (더미 데이터 삽입)
        User user = createUniqueUser("list");
        for (int i = 1; i <= 3; i++) {
            Post post = Post.builder()
                    .title("테스트 게시글 " + i)
                    .content("이것은 " + i + "번째 테스트 게시글입니다.")
                    .user(user)
                    .build();
            em.persist(post);
        }
        em.flush();
        em.clear();

        // when
        List<Post> postList = em.createQuery("select p from Post p", Post.class)
                .getResultList();

        // then
        assertThat(postList).isNotEmpty();
        System.out.println("✅ 전체 게시글 수: " + postList.size());
        postList.forEach(p ->
                System.out.printf("ID=%d | TITLE=%s | WRITER=%s%n",
                        p.getId(), p.getTitle(), p.getUser().getNickname())
        );
    }

    // ✅ 매번 다른 이메일의 유저를 생성하는 유틸 메서드
    private User createUniqueUser(String prefix) {
        String email = prefix + "_" + System.currentTimeMillis() + "@adapterz.kr";
        User user = new User(email, "pw123", prefix);
        em.persist(user);
        return user;
    }
}
