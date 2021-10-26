package com.shoppingmall.domain.items;

import com.shoppingmall.enums.Grade;
import com.shoppingmall.domain.items.repository.BasketItemRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BasketItemRepositoryTest {

    @Autowired BasketItemRepository basketItemRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;
    @PersistenceContext EntityManager em;

    @Test
    @DisplayName("품절된 상품이 모두 한 번에 삭제된다")
    void deleteAllSoldOutAndFindBasketItemWithItemById() {

        Member member = new Member("member1", "member111", "member111#", Grade.USER, true);
        Upper knit = Upper.createUpper("니트1", 70000, 0, member, 50, 70, 40);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 0, member, 100, 3, 45);

        memberRepository.save(member);
        itemRepository.save(knit);
        itemRepository.save(jacket1);

        BasketItem basketItem1 = BasketItem.createBasketItem(knit, member,2);
        BasketItem basketItem2 = BasketItem.createBasketItem(jacket1, member,2);
        BasketItem saved1 = basketItemRepository.save(basketItem1);
        BasketItem saved2 = basketItemRepository.save(basketItem2);

        em.flush();
        em.clear();

        BasketItem findBI1 = basketItemRepository.findBasketItemWithItemById(saved1.getId()).get();
        BasketItem findBI2 = basketItemRepository.findBasketItemWithItemById(saved2.getId()).get();

        findBI1.statusUpdate();
        findBI2.statusUpdate();

        basketItemRepository.deleteBySoldOut();
        List<BasketItem> all = basketItemRepository.findAll();

        assertThat(all.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("선택된 장바구니 아이템들을 한번에 삭제")
   void deleteByIds() throws Exception {

        // given
        Member member = new Member("member1", "member111", "member111#", Grade.USER, true);
        Upper knit = Upper.createUpper("니트1", 70000, 0, member, 50, 70, 40);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 0, member, 100, 3, 45);

        memberRepository.save(member);
        itemRepository.save(knit);
        itemRepository.save(jacket1);

        BasketItem basketItem1 = BasketItem.createBasketItem(knit, member,2);
        BasketItem basketItem2 = BasketItem.createBasketItem(jacket1, member,2);
        BasketItem saved1 = basketItemRepository.save(basketItem1);
        BasketItem saved2 = basketItemRepository.save(basketItem2);

        em.flush();

        // when
        basketItemRepository.deleteByIds(Arrays.asList(saved1.getId(), saved2.getId()));

        // then
        List<BasketItem> all = basketItemRepository.findAll();

        assertThat(all.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("멤버id로_장바구니_아이템_조회")
    void 멤버id로_장바구니_아이템_조회() throws Exception {

        // given
        Member member = new Member("member1", "member111", "member111#", Grade.USER, true);
        Upper knit = Upper.createUpper("니트1", 70000, 0, member, 50, 70, 40);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 0, member, 100, 3, 45);

        Member savedMember = memberRepository.save(member);
        itemRepository.save(knit);
        itemRepository.save(jacket1);

        BasketItem basketItem1 = BasketItem.createBasketItem(knit, member,2);
        BasketItem basketItem2 = BasketItem.createBasketItem(jacket1, member,2);
        basketItemRepository.save(basketItem1);
        basketItemRepository.save(basketItem2);

        em.flush();

        // when
        List<BasketItem> basketItems = basketItemRepository.findBasketItemsByMember(savedMember);

        // then
        assertThat(basketItems.size()).isEqualTo(2);

    }
}