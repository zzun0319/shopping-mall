package com.shoppingmall.domain;

import com.shoppingmall.domain.commons.BaseDateInfo;
import com.shoppingmall.exceptions.CannotSaleItemException;
import com.shoppingmall.exceptions.NotEnoughStockException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Item extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "item_id")
    protected Long id;

    protected String name;
    protected Integer price;
    protected Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    protected Member salesman;

    protected Item(String name, Integer price, Integer stockQuantity, Member salesman) {
        if(!salesman.getSaleAvailable()){
            throw new CannotSaleItemException("상품 판매 허가가 나지 않았습니다.");
        }
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.salesman = salesman;
    }

    /**
     * 누군가 상품을 주문했을 때 재고에서 주문수량만큼 감소시킨다.
     */
    public void reduceStockQuantity(int cnt) {
        int restStock = stockQuantity - cnt;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다");
        }
        this.stockQuantity -= cnt;
    }

    /**
     * 누군가 상품을 주문을 취소했을 때 취소한 수량만큼 재고를 증가시킨다.
     */
    public void addStockQuantity(int cnt) {
        this.stockQuantity += cnt;
    }
}
