package com.shoppingmall.domain.items.repository;

import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.members.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 상품 종류(상의,하의,외투) 별 조회
     * @param type
     * @param pageable
     * @return
     */
    @Query("SELECT i FROM Item i WHERE dType = :type")
    Page<Item> findClass(@Param("type") String type, Pageable pageable);

    /**
     * 상품 id로 상품과 상품판매자 페치 조인
     * @param id
     * @return
     */
    @Query("SELECT i FROM Item i JOIN FETCH i.salesman WHERE i.id = :id")
    Optional<Item> findItemAndSalesmanById(@Param("id") Long id);

    /**
     * 가격 별 조회 (~만원 이하, 뷰에는 3개로 만들고 버튼. 3만, 5만, 10만)
     * @param price
     * @param pageable
     * @return
     */
    Page<Item> findByPriceLessThanEqual(int price, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.salesman = :salesmanId")
    Page<Item> findItems(@Param("salesmanId") Member salesman, Pageable pageable);
}
