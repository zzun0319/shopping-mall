package com.shoppingmall.domain.members;

import com.shoppingmall.enums.Grade;
import com.shoppingmall.domain.members.forms.ChangePasswordForm;
import com.shoppingmall.domain.members.forms.MemberJoinForm;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.members.service.MemberService;
import com.shoppingmall.exceptions.NoSuchMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("아이디 중복 체크 메서드")
    void checkDuplicateId() {

        // given
        Member member = Member.createMember("kim", "kim123", "k1235#");
        em.persist(member);

        // when
        boolean result1 = memberService.checkDuplicateId("kim123");
        boolean result2 = memberService.checkDuplicateId("kim123pppp");

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("회원가입 메서드")
    void join() throws IOException {

        // given
        MemberJoinForm memberJoinForm = new MemberJoinForm("kim5989", "kim3333#", "park");

        // when
        Long joinedMemberId = memberService.join(memberJoinForm);

        // then
        Optional<Member> om = memberRepository.findById(joinedMemberId);
        Member member = om.orElseThrow(() -> new NoSuchMemberException());

        assertThat(member.getSaleAvailable()).isFalse();
        assertThat(member.getGrade()).isEqualTo(Grade.USER);
        assertThat(member.getLoginId()).isEqualTo("kim5989");

    }

    @Test
    @DisplayName("비밀번호 변경 메서드 성공") // 기존 비밀번호와 일치하지 않을 때 컨트롤러에서 서비스까지 넘어오지 않고, 컨트롤러에서 에러를 담아서 뷰로 넘기도록
    void passwordChange() throws Exception {

        // given
        MemberJoinForm memberJoinForm = new MemberJoinForm("kim5989", "kim3333#", "park");
        Long joinedMemberId = memberService.join(memberJoinForm);

        Member member = memberRepository.findById(joinedMemberId).get();

        ChangePasswordForm changePasswordForm
                = new ChangePasswordForm("kim3333#", "new333@", "new333@");

        // when
        Long changedMemberId = memberService.passwordChange(member, changePasswordForm);

        // then
        Optional<Member> om = memberRepository.findById(changedMemberId);
        Member findMember = om.orElseThrow(() -> new NoSuchMemberException());

        assertThat(joinedMemberId).isEqualTo(changedMemberId);
        assertThat(findMember.getLoginId()).isEqualTo("kim5989");

    }

}