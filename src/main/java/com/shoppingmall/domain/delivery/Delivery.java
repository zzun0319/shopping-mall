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

    public void startDelivery() {
        this.status = DeliveryStatus.ING;
    }

    public void cancelDelivery() {
        this.status = DeliveryStatus.CANCEL;
    }

    public void completeDelivery() {
        this.status = DeliveryStatus.COMPLETE;
    }

    public void addressChange(Address address) {
        if(this.status != DeliveryStatus.BEFORE) { // 서비스로 넘겨 이거
            throw new CannotChangeAddressException("배송 시작 전에만 변경 가능합니다");
        }
        this.address = address;
    }
}
