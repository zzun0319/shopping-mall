package com.shoppingmall.domain.members;

import com.shoppingmall.domain.enums.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 id로 회원 조회하기")
    void findByLoginId() {

        // given
        Member member = Member.createMember("kim", "kim123", "k1235#");
        em.persist(member);

        // when
        Optional<Member> om = memberRepository.findByLoginId("kim123");
        Member findMember = om.orElse(Member.createMember("new", "new123", "new1234!"));

        // then
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    @DisplayName("회원 등급별로 회원 리스트를 받기")
    void findByGrade() throws Exception {

        // given
        Member member1 = new Member("name1", "loginid1", "pass123", Grade.VIP, true);
        Member member2 = new Member("name2", "loginid2", "pass123", Grade.USER, true);
        Member member3 = new Member("name3", "loginid3", "pass123", Grade.VIP, false);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        // when
        List<Member> result = memberRepository.findByGrade(Grade.VIP);

        // then
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("상품 판매가 가능한 회원인지 아닌지에 따라 회원 리스트 받기")
    void findBySaleAvailable() throws Exception {

        // given
        Member member1 = new Member("name1", "loginid1", "pass123", Grade.VIP, true);
        Member member2 = new Member("name2", "loginid2", "pass123", Grade.USER, true);
        Member member3 = new Member("name3", "loginid3", "pass123", Grade.VIP, false);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        // when
        List<Member> result = memberRepository.findBySaleAvailable(false);

        // then
        assertThat(result.size()).isEqualTo(1);

    }
}