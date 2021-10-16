package com.shoppingmall.domain.items.repository;

import com.shoppingmall.domain.items.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE dType = :type")
    Page<Item> findClass(@Param("type") String type, Pageable pageable);

    @Query("SELECT i FROM Item i JOIN FETCH i.salesman WHERE i.id = :id")
    Optional<Item> findItemAndSalesmanById(@Param("id") Long id);

    // 가격 별 조회 (~만원 이하, 뷰에는 3개로 만들고 버튼. 3만, 5만, 10만)
    Page<Item> findByPriceLessThanEqual(int price, Pageable pageable);
}
