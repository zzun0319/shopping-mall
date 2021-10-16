package com.shoppingmall.domain.orders.repository;

import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 한 회원의 모든 주문 조회
     * @param member
     * @return
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.member WHERE o.member = :member")
    List<Order> findOrdersByMember(@Param("member") Member member);

}
