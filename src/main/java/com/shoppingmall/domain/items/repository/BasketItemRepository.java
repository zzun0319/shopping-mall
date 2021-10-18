package com.shoppingmall.domain.items.repository;

import com.shoppingmall.domain.items.BasketItem;
import com.shoppingmall.domain.members.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    /**
     * 품절 상품 모두 장바구니에서 삭제
     * @return 삭제된 튜플의 수
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BasketItem b WHERE b.soldOut = true")
    int deleteBySoldOut();

    /**
     * 선택 상품 장바구니에서 삭제
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BasketItem b WHERE b.id in :ids")
    void deleteByIds(@Param("ids") Collection<Long> ids);

    @Query("SELECT b FROM BasketItem b JOIN FETCH b.item WHERE b.id = :id")
    Optional<BasketItem> findBasketItemWithItemById(@Param("id") Long id);

    @Query("SELECT b FROM BasketItem b JOIN FETCH b.item")
    List<BasketItem> findAllWithItems();

    @EntityGraph(attributePaths = {"item"})
    List<BasketItem> findBasketItemsByMember(Member member);
}
