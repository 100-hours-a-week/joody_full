package com.example.assignment_4.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class LikeTest {

    @PersistenceContext
    private EntityManager em;

    // ✅ 좋아요 저장 테스트
    @Test
    @DisplayName("✅ Like 엔티티가 정상적으로 저장된다")
    void saveLikeEntityTest() {
        // given
        User user = createUniqueUser("like_tester");
        Post post = createPost(user, "좋아요 테스트 게시글", "좋아요 테스트 내용");

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();

        // when
        em.persist(like);
        em.flush();
        em.clear();

        // then
        Like foundLike = em.find(Like.class, like.getId());
        assertThat(foundLike).isNotNull();
        assertThat(foundLike.getUser().getEmail()).contains("like_tester");
        assertThat(foundLike.getPost().getTitle()).isEqualTo("좋아요 테스트 게시글");

        System.out.println("✅ 저장된 Like ID: " + foundLike.getId());
        System.out.println("✅ 좋아요 누른 유저: " + foundLike.getUser().getNickname());
        System.out.println("✅ 좋아요 대상 게시글: " + foundLike.getPost().getTitle());
        System.out.println("✅ createdAt: " + foundLike.getCreatedAt());
    }

    // ✅ 좋아요 여러 개 저장 및 조회 테스트
    @Test
    @DisplayName("✅ 여러 Like 엔티티를 저장하고 조회할 수 있다")
    void findAllLikesTest() {
        // given
        User user = createUniqueUser("multi_like");
        Post post1 = createPost(user, "게시글 1", "내용 1");
        Post post2 = createPost(user, "게시글 2", "내용 2");

        Like like1 = Like.builder().user(user).post(post1).build();
        Like like2 = Like.builder().user(user).post(post2).build();

        em.persist(like1);
        em.persist(like2);
        em.flush();
        em.clear();

        // when
        List<Like> likes = em.createQuery("select l from Like l", Like.class)
                .getResultList();

        // then
        assertThat(likes).hasSize(2);
        System.out.println("✅ 전체 좋아요 수: " + likes.size());
        likes.forEach(l ->
                System.out.printf("LIKE_ID=%d | USER=%s | POST_TITLE=%s%n",
                        l.getId(), l.getUser().getNickname(), l.getPost().getTitle())
        );
    }

    // ✅ 유틸: 매번 다른 유저 생성
    private User createUniqueUser(String prefix) {
        String email = prefix + "_" + System.currentTimeMillis() + "@adapterz.kr";
        User user = new User(email, "pw123", prefix);
        em.persist(user);
        return user;
    }

    // ✅ 유틸: 게시글 생성
    private Post createPost(User user, String title, String content) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
        em.persist(post);
        return post;
    }
}
