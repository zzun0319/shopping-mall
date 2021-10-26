package com.shoppingmall.domain.orders.repository;

import com.shoppingmall.enums.DeliveryStatus;
import com.shoppingmall.enums.PaymentStatus;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 한 회원의 모든 주문 조회
     * QueryHint를 써서 조회에만 사용한다고 명시. 가져올 때 스냅샷 X. 따라서 더티체킹도 X.
     * @param member
     * @return
     */
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
    @EntityGraph(attributePaths = {"member", "payment", "delivery"})
    Page<Order> findOrdersDeliveryPaymentByMember(@Param("member") Member member, Pageable pageable);

    /**
     * 주문 번호로 주문 찾기 (결제 정보 함께 가져오기)
     * @param orderId
     * @return
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.payment WHERE o.id = :orderId")
    Optional<Order> findOrderWithPaymentByOrderId(@Param("orderId") Long orderId);

    /**
     * 주문 번호로 주문 찾기 (결제 정보, 배송 정보도 함께 가져오기)
     * @param orderId
     * @return
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.payment JOIN FETCH o.delivery WHERE o.id = :orderId")
    Optional<Order> findOrderWithPaymentAndDeliveryByOrderId(@Param("orderId") Long orderId);

    /**
     * 주문 번호로 주문 찾기 (배송 정보 함께 가져오기)
     * @param orderId
     * @return
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.delivery WHERE o.id = :orderId")
    Optional<Order> findOrderWithDeliveryByOrderId(@Param("orderId") Long orderId);

    /**
     * 결제 상태와 배송 상태로 주문 찾기
     * @param d_status
     * @param p_status
     * @param pageable
     * @return
     */
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
    @EntityGraph(attributePaths = {"payment", "delivery"})
    @Query("SELECT o FROM Order o WHERE o.delivery.status = :d_status AND o.payment.status = :p_status")
    Page<Order> findOrdersWithDelAndPay(@Param("d_status") DeliveryStatus d_status,
                                        @Param("p_status") PaymentStatus p_status, Pageable pageable);
}
