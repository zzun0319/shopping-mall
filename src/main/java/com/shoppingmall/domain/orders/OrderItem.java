package com.shoppingmall.domain.orders;

import com.shoppingmall.domain.items.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer totalPrice; // 주문 가격 = 주문 수량 * 상품 가겨
    private Integer orderQuantity; // 주문 수량

    private OrderItem(Item item, Integer totalPrice, Integer orderQuantity) {
        this.item = item;
        this.totalPrice = totalPrice;
        this.orderQuantity = orderQuantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * OrderItem 생성 메서드
     * @param item 주문상품
     * @param orderQuantity 주문 수량
     * @return 필드를 채운 orderItem 객체
     */
    public static OrderItem createOrderItem(Item item, int orderQuantity) { // item에 price가 있지만, 쿠폰 할인 같은 게 적용된 가격이 들어오도록
        OrderItem orderItem = new OrderItem(item, item.getPrice() * orderQuantity, orderQuantity);
        item.reduceStockQuantity(orderQuantity);
        return orderItem;
    }

    /**
     * 취소 시 상품의 재고를 취소수량 만큼 다시 늘려준다.
     */
    public void cancel() {
        getItem().addStockQuantity(orderQuantity);
    }

    /**
     * 주문 상품의 가격 X 주문 수량
     * @return
     */
    public int getTotalPrice() {
        return totalPrice;
    }
}
