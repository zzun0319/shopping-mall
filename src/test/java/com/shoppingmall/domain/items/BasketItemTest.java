package com.shoppingmall.domain.items;

import com.shoppingmall.domain.enums.Grade;
import com.shoppingmall.domain.members.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class BasketItemTest {

    @Test
    @DisplayName("상품의 수량보다 적을 경우, 품절 상태여야 한다")
    public void soldOut() {
        Member member = new Member("member1", "member111", "member111#", Grade.USER, true);
        Upper knit = Upper.createUpper("니트1", 70000, 1, member, 50, 70, 40);

        BasketItem basketItem = BasketItem.createBasketItem(knit, member,2);
        assertThat(basketItem.isSoldOut()).isFalse();
        basketItem.statusUpdate();
        assertThat(basketItem.isSoldOut()).isTrue();
    }

}