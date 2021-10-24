package com.shoppingmall.domain.delivery;

import com.shoppingmall.domain.commons.BaseDateInfo;
import com.shoppingmall.domain.enums.DeliveryStatus;
import com.shoppingmall.domain.valuetype.Address;
import com.shoppingmall.exceptions.CannotChangeAddressException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public Delivery(Address address) {
        this.address = address;
        this.status = DeliveryStatus.BEFORE;
    }

    /**
     * 배송 시작
     */
    public void startDelivery() {
        this.status = DeliveryStatus.ING;
    }

    /**
     * 배송 취소
     */
    public void cancelDelivery() {
        this.status = DeliveryStatus.CANCEL;
    }

    /**
     * 배송 완료
     */
    public void completeDelivery() {
        this.status = DeliveryStatus.COMPLETE;
    }

    /**
     * 배송지 변경
     * @param address
     */
    public void addressChange(Address address) {
        if(this.status != DeliveryStatus.BEFORE) { // 서비스로 넘겨 이거
            throw new CannotChangeAddressException("배송 시작 전에만 변경 가능합니다");
        }
        this.address = address;
    }
}
