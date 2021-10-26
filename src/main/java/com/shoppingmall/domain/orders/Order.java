package com.shoppingmall.domain.orders;

import com.shoppingmall.domain.delivery.Delivery;
import com.shoppingmall.enums.PaymentOption;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.payment.Payment;
import com.shoppingmall.domain.commons.BaseDateInfo;
import com.shoppingmall.enums.DeliveryStatus;
import com.shoppingmall.enums.OrderStatus;
import com.shoppingmall.exceptions.CannotCancelException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(cascade = ALL, mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Order(Member member, Delivery delivery, Payment payment) {
        this.member = member;
        this.delivery = delivery;
        this.payment = payment;
        this.status = OrderStatus.ORDER;
    }

    /**
     * 연관관계 양쪽에 세팅해주기 위한 메서드
     * @param orderItem
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Order 생성 메서드
     * @param member 주문한 사람
     * @param delivery 배송 정보
     * @param payment 결제 정보
     * @param orderItems 주문 상품들
     * @return 필드를 채운 주문 객체
     */
    public static Order createOrder(Member member, Delivery delivery, Payment payment, List<OrderItem> orderItems){
        Order order = new Order(member, delivery, payment);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return order;
    }

    /**
     * 주문 취소 메서드
     */
    public void cancelOrder() {

        if(delivery.getStatus() != DeliveryStatus.BEFORE){ // 배송 시작 전에만 취소 가능
            throw new CannotCancelException("이미 배송완료된 상품은 취소가 불가능합니다.");
        } // 이런 예외들은 다 서비스로 빼야겠다

        delivery.changeDelivery(DeliveryStatus.CANCEL);
        status = OrderStatus.CANCEL;
        for (OrderItem orderItem : getOrderItems()) {
            orderItem.cancel();
        }
    }

    /**
     * 주문 금액 결제 메서드
     * @param option
     */
    public void pay(Integer paidPrice, PaymentOption option) {
        payment.pay(paidPrice, option);
    }

    /**
     * 주문 총금액 리턴해주는 메서드
     * @return
     */
    public int getTotalOrderPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

}
