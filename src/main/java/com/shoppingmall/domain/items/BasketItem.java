package com.shoppingmall.domain.items;

import com.shoppingmall.domain.commons.BaseDateInfo;
import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.members.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasketItem extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "basket_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int quantity;
    private boolean soldOut;

    private BasketItem(Item item, Member member, int quantity) {
        this.item = item;
        this.member = member;
        this.quantity = quantity;
        this.soldOut = false;
    }

    /**
     * 생성 메서드
     * @return
     */
    public static BasketItem createBasketItem(Item item, Member member, int quantity) {
        return new BasketItem(item, member, quantity);
    }

    /**
     * 장바구니 리스트 불러올 때, 호출할 것.
     */
    public void statusUpdate(){
        if (item.getStockQuantity() < quantity) {
            soldOut = true;
        }
    }

    public int getTotalPrice() {
        return quantity * item.getPrice();
    }
}
