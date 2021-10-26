package com.shoppingmall;

import com.shoppingmall.enums.Grade;
import com.shoppingmall.domain.items.ImageFile;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.items.repository.ImageFileRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.AttachedFile;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
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
        private final ImageFileRepository imageFileRepository;

        public void dbInitMember() {
            Member admin = new Member("admin", "admin123", "admin123#", Grade.ADMIN, false);
            Member seller1 = new Member("seller1", "seller111", "aaa1111#", Grade.VIP, true);
            seller1.setFile(new AttachedFile("판매허가증1.txt", "member1.txt"));
            Member seller2 = new Member("seller2", "seller222", "aaa1111#", Grade.USER, true);
            seller2.setFile(new AttachedFile("판매허가증2.txt", "member2.txt"));
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
            Outer coat1 = itemRepository.save(coat);
            imageFileRepository.save(new ImageFile("coat1.jpg", "coat1.jpg", coat1));
            imageFileRepository.save(new ImageFile("coat2.jpg", "coat2.jpg", coat1));
            imageFileRepository.save(new ImageFile("coat3.jpg", "coat3.jpg", coat1));

            Pants pants1 = itemRepository.save(jean);
            imageFileRepository.save(new ImageFile("jean1.jpg", "jean1.jpg", pants1));
            imageFileRepository.save(new ImageFile("jean2.jpg", "jean2.jpg", pants1));

            Upper shirt1 = itemRepository.save(tShirt);
            imageFileRepository.save(new ImageFile("tshirt1.jpg", "tshirt1.jpg", shirt1));
            imageFileRepository.save(new ImageFile("tshirt2.jpg", "tshirt2.jpg", shirt1));

            itemRepository.save(coat2);

            Pants cottonPants = itemRepository.save(pants);
            imageFileRepository.save(new ImageFile("cottonPants1.jpg", "cottonPants1.jpg", cottonPants));
            imageFileRepository.save(new ImageFile("cottonPants2.jpg", "cottonPants2.jpg", cottonPants));

            Upper knit1 = itemRepository.save(knit);
            imageFileRepository.save(new ImageFile("knit1.jpg", "knit1.jpg", knit1));
            imageFileRepository.save(new ImageFile("knit2.jpg", "knit2.jpg", knit1));
        }
    }
}
