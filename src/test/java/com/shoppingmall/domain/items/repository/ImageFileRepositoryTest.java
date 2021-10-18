package com.shoppingmall.domain.items.repository;

import com.shoppingmall.domain.items.ImageFile;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ImageFileRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("item으로ImageFile찾기")
    void item으로ImageFile찾기() throws Exception {

        // given
        Member seller1 = memberRepository.findByLoginId("seller111").get();
        Outer coat = Outer.createOuter("코트5", 200000, 10, seller1, 110, 3, 50);

        Outer coat1 = itemRepository.save(coat);
        imageFileRepository.save(new ImageFile("coat4.jpg", "coat4.jpg", coat1));
        imageFileRepository.save(new ImageFile("coat5.jpg", "coat5.jpg", coat1));
        imageFileRepository.save(new ImageFile("coat6.jpg", "coat6.jpg", coat1));

        em.flush();
        em.clear();

        // when
        List<ImageFile> imageFiles = imageFileRepository.findImageFilesByItem(coat1);

        // then
        Assertions.assertThat(imageFiles.size()).isEqualTo(3);
        Assertions.assertThat(imageFiles.get(0).getOriginalFileName()).isEqualTo("coat4.jpg");
    }

}