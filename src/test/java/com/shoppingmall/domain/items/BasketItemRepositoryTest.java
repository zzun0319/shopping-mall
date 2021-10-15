package com.shoppingmall.domain.items;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BasketItemRepositoryTest {

    @Autowired
    BasketItemRepository basketItemRepository;

    @Test
    @DisplayName("품절한 상품이 모두 한 번에 삭제된다")
    void deleteAllSoldOut() {



    }
}