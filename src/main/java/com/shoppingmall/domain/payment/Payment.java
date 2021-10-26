package com.shoppingmall.domain.payment;

import com.shoppingmall.domain.commons.BaseDateInfo;
import com.shoppingmall.enums.PaymentOption;
import com.shoppingmall.enums.PaymentStatus;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Payment extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentOption option;

    private Integer paidPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /**
     * 기본 생성자. 주문이랑 결제랑 시점이 다르기 때문에 빈 Payment 객체를 생성해서 Order에 넣어두어야하니까
     */
    public Payment() {
        this.option = null;
        this.paidPrice = 0;
        this.status = PaymentStatus.NOT_YET;
    }

    /**
     * 결제 메서드(이를 호출하는 곳에서 paidPrice와 주문 총금액이 같은지 비교한 뒤 호출)
     * @param paidPrice
     */
    public void pay(Integer paidPrice, PaymentOption option) {
        this.option = option;
        this.paidPrice = paidPrice;
        this.status = PaymentStatus.COMPLETE;
    }

}
