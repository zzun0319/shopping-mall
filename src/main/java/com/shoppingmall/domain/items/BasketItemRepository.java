package com.shoppingmall.domain.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    /**
     * 품절 상품 전체 삭제
     * @return 삭제된 튜플의 수
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BasketItem b WHERE b.soldOut = true")
    int deleteBySoldOut();

    // 선택 상품 삭제.(이건 서비스겠다)
}
