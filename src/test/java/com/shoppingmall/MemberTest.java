package com.shoppingmall;

import com.shoppingmall.domain.members.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("비밀번호를 같이 넘겨주어야 상품 판매 등록 권한을 부여할 수 있다")
    void permitSale() {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");

        // when
        member.permitSale("abc1234");

        // then
        assertThat(member.getSaleAvailable()).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 판매 등록 권한을 부여할 수 없다")
    void cannotSale() {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");

        // when
        member.permitSale("adore32"); // 판매 허가를 위한 비밀번호 틀렸을 때

        // then
        assertThat(member.getSaleAvailable()).isFalse();
    }

}