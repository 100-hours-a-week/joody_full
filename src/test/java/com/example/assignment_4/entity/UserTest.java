package com.example.assignment_4.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class UserTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("User 엔티티가 정상적으로 저장된다")
    void saveUserEntityTest() {
        // given
        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz");
        user.setProfileImage("default.png");

        // when
        em.persist(user);
        em.flush(); // 실제 DB에 반영
        em.clear(); // 1차 캐시 비움 → 진짜 DB에서 다시 조회

        // then
        User foundUser = em.find(User.class, user.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("tester@adapterz.kr");
        assertThat(foundUser.getNickname()).isEqualTo("Adapterz");

        System.out.println("✅ 저장된 User ID: " + foundUser.getId());
        System.out.println("✅ createdAt: " + foundUser.getCreatedAt());
        System.out.println("✅ updatedAt: " + foundUser.getUpdatedAt());
    }

    @Test
    @DisplayName("User 엔티티가 수정될 때 updatedAt이 갱신된다")
    void updateUserEntityTest() throws InterruptedException {
        // given
        User user = new User("update@adapterz.kr", "pw123", "beforeUpdate");
        user.setProfileImage("old.png");
        em.persist(user);
        em.flush();
        em.clear();

        // when
        User foundUser = em.find(User.class, user.getId());
        LocalDateTime beforeUpdate = foundUser.getUpdatedAt();

        Thread.sleep(1000); // updatedAt 차이 보기 위해 1초 대기
        foundUser.setNickname("afterUpdate");
        foundUser.setProfileImage("new.png");
        em.flush();
        em.clear();

        // then
        User updatedUser = em.find(User.class, user.getId());
        assertThat(updatedUser.getUpdatedAt()).isAfter(beforeUpdate);
        assertThat(updatedUser.getNickname()).isEqualTo("afterUpdate");

        System.out.println("✅ updatedAt 변경 전: " + beforeUpdate);
        System.out.println("✅ updatedAt 변경 후: " + updatedUser.getUpdatedAt());
    }

    @Test
    @DisplayName("모든 유저를 조회할 수 있다")
    void findAllUsersTest() {
        // given
        List<User> userList = em.createQuery("select u from User u", User.class)
                .getResultList();

        // then
        assertThat(userList).isNotEmpty();
        System.out.println("✅ 전체 유저 수: " + userList.size());
        userList.forEach(u ->
                System.out.printf("ID=%d, EMAIL=%s, NICKNAME=%s%n", u.getId(), u.getEmail(), u.getNickname())
        );
    }
}
