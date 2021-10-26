package com.shoppingmall.domain.items;

import com.shoppingmall.enums.Grade;
import com.shoppingmall.domain.items.forms.ItemRegisterForm;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.items.service.ItemService;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.exceptions.NotEnoughStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;

    @PersistenceContext EntityManager em;

    @Test
    @DisplayName("상품 종류별 폼에 작성한 데이터를 넘기면, memberService가 분류해서 저장해준다")
    void saveItem() {

        // given
        Member salesman = new Member("s1", "ss111", "ssss1111@", Grade.USER, true);
        memberRepository.save(salesman);

        ItemRegisterForm form = new ItemRegisterForm("jean1", 50000, 10, salesman.getId(), "U", 95, 28, 40);

        // when
        Item saveItem = itemService.saveItem(form, salesman);

        // then
        Item findItem = itemRepository.findById(saveItem.getId()).get();
        assertThat(findItem.getName()).isEqualTo("jean1");
        assertThat(findItem.getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("상품 판매자가 아이템의 재고를 늘이고 줄일 수 있는 메서드")
    void addAndReduceQuantity() throws Exception {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSaleChange("abc1234", true);
        memberRepository.save(member);

        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, member, 32, 71, 58);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 10, member, 100, 3, 45);

        Upper savedUpper = itemRepository.save(upper);
        Outer savedOuter = itemRepository.save(jacket1);

        em.flush();
        em.clear();

        // when
        itemService.addQuantity(member.getId(), savedUpper.getId(), 3);
        itemService.reduceQuantity(member.getId(), savedOuter.getId(), 5);

        // then
        Item findUpper = itemRepository.findById(savedUpper.getId()).get();
        assertThat(findUpper.getStockQuantity()).isEqualTo(8);

        Item findOuter = itemRepository.findById(savedOuter.getId()).get();
        assertThat(findOuter.getStockQuantity()).isEqualTo(5);

    }

    @Test
    @DisplayName("재고를 0보다 작게 만들 수는 없다")
    void reduceQuantityFail() throws Exception {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSaleChange("abc1234", true);
        Member savedMember = memberRepository.save(member);

        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, member, 32, 71, 58);
        Upper savedUpper = itemRepository.save(upper);

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(NotEnoughStockException.class, () -> itemService.reduceQuantity(savedMember.getId(), savedUpper.getId(), 8));

    }

}
