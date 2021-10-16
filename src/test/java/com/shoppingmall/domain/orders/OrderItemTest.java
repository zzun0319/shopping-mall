package com.shoppingmall.domain.orders;

import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.orders.OrderItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("orderItem에서 주문한 개수만큼 item에서는 재고가 줄었고, 주문 총합 금액이 맞아야한다.")
    void createOrderItemTest() {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSale("abc1234");

        Pants jean1 = Pants.createPants("Jean1", 50000, 3, member, 100, 28, 90);

        // when
        OrderItem orderItem = OrderItem.createOrderItem(jean1, jean1.getPrice(), 2);

        // then
        assertThat(jean1.getStockQuantity()).isEqualTo(1);
        assertThat(orderItem.getTotalPrice()).isEqualTo(100000);
    }

    @Test
    @DisplayName("취소하면 상품 재고가 다시 원래대로 돌아온다.")
    void cancel() {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSale("abc1234");

        Upper shirt1 = Upper.createUpper("T-shirt1", 30000, 5, member, 32, 71, 58);
        OrderItem orderItem = OrderItem.createOrderItem(shirt1, shirt1.getPrice(), 2);

        assertThat(shirt1.getStockQuantity()).isEqualTo(3);

        // when
        orderItem.cancel();

        // then
        assertThat(shirt1.getStockQuantity()).isEqualTo(5);
    }

}