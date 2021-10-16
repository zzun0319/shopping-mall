package com.shoppingmall;

import com.shoppingmall.domain.enums.Grade;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitDbService initDbService;

    @PostConstruct
    public void init() {
        initDbService.dbInitMember();
        initDbService.dbInitItem();
    }

    @Component
    @RequiredArgsConstructor
    private static class InitDbService {

        private final MemberRepository memberRepository;
        private final ItemRepository itemRepository;

        public void dbInitMember() {
            Member admin = new Member("admin", "admin123", "admin123#", Grade.ADMIN, false);
            Member seller1 = new Member("seller1", "seller111", "aaa1111#", Grade.VIP, true);
            Member seller2 = new Member("seller2", "seller222", "aaa1111#", Grade.USER, true);
            Member buyer1 = new Member("buyer1vip", "bbb111", "aaa1111#", Grade.VIP, false);
            Member buyer2 = Member.createMember("buyer2", "bbb222", "aaa1111#");
            memberRepository.save(admin);
            memberRepository.save(seller1);
            memberRepository.save(seller2);
            memberRepository.save(buyer1);
            memberRepository.save(buyer2);
        }

        public void dbInitItem() {
            Member seller1 = memberRepository.findByLoginId("seller111").get();
            Outer coat = Outer.createOuter("코트", 200000, 10, seller1, 110, 3, 50);
            Pants jean = Pants.createPants("청바지", 70000, 10, seller1, 95, 28, 40);
            Upper tShirt = Upper.createUpper("티셔츠", 30000, 10, seller1, 60, 70, 60);
            Outer coat2 = Outer.createOuter("트렌치코트", 150000, 10, seller1, 110, 2, 50);
            Pants pants = Pants.createPants("면바지", 50000, 10, seller1, 95, 28, 40);
            Upper knit = Upper.createUpper("니트", 60000, 10, seller1, 60, 70, 60);
            itemRepository.save(coat);
            itemRepository.save(jean);
            itemRepository.save(tShirt);
            itemRepository.save(coat2);
            itemRepository.save(pants);
            itemRepository.save(knit);
        }
    }
}
