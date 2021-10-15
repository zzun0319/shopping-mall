package com.shoppingmall.domain.items;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 상품 종류별 조회 (상의, 하의, 외투)
    Page<Item> findByDType(String dType, Pageable pageable);

    // 가격 별 조회 (~만원 이하, 뷰에는 3개로 만들고 버튼. 3만, 5만, 10만)
    Page<Item> findByPriceLessThan(int price, Pageable pageable);
}
