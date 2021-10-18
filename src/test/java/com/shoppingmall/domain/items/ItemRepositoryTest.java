package com.shoppingmall.domain.items;

import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
public class ItemRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;
    @PersistenceContext EntityManager em;

    @Test
    @DisplayName("상품id로 Item과 등록한 멤버를 페치조인")
    void findItemAndSalesmanById() throws Exception {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSaleChange("abc1234", true);
        memberRepository.save(member);

        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, member, 32, 71, 58);
        Upper savedUpper = itemRepository.save(upper);

        em.flush();
        em.clear();

        // when
        Item item = itemRepository.findItemAndSalesmanById(savedUpper.getId()).get();

        // then
        assertThat(item.getName()).isEqualTo(upper.getName());

    }

    @Test
    @DisplayName("입력된 가격보다 가격이 낮은 아이템들을 조회한다")
    void findByPriceLessThanEqual() throws Exception {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSaleChange("abc1234", true);
        memberRepository.save(member);

        Upper tShirt = Upper.createUpper("tShirt", 30000, 10, member, 50, 70, 40);
        Pants pants1 = Pants.createPants("pants1", 25000, 10, member, 100, 30, 50);
        Outer jacket1 = Outer.createOuter("jacket1", 100000, 10, member, 100, 3, 60);
        itemRepository.save(tShirt);
        itemRepository.save(pants1);
        itemRepository.save(jacket1);

        em.flush();
        em.clear();

        // when
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Item> page = itemRepository.findByPriceLessThanEqual(30000, pageRequest);

        // then
        assertThat(page.getContent().size()).isEqualTo(2);
//        for (Item item : page) {
//            System.out.println("item = " + item.getName());
//        }

    }

    @Test
    @DisplayName("상품 종류별 조회, 페이징")
    void findByItemType() throws Exception {

        // given
        Member member = Member.createMember("memberA", "aaa1111", "aaa#1111");
        member.permitSaleChange("abc1234", true);
        memberRepository.save(member);

        Upper tShirt = Upper.createUpper("tShirt", 30000, 10, member, 50, 70, 40);
        Pants pants1 = Pants.createPants("pants1", 25000, 10, member, 100, 30, 50);
        Outer jacket1 = Outer.createOuter("jacket1", 100000, 10, member, 100, 3, 60);
        itemRepository.save(tShirt);
        itemRepository.save(pants1);
        itemRepository.save(jacket1);

        // when
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Item> page = itemRepository.findClass("U", pageRequest);

        // then
        assertThat(page.getTotalPages()).isEqualTo(1);
//        for (Item item : page) {
//            System.out.println("item = " + item.getName());
//        }
    }
}
