package com.shoppingmall;

import com.shoppingmall.domain.delivery.Delivery;
import com.shoppingmall.domain.enums.DeliveryStatus;
import com.shoppingmall.domain.valuetype.Address;
import com.shoppingmall.exceptions.CannotChangeAddressException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DeliveryTest {

    @Test
    @DisplayName("배송 대기 상태일 때만 배송지를 바꿀 수 있다")
    void addressChange() {

        // given
        Delivery delivery = new Delivery(new Address("city1", "street1", "11111"));

        // when
        delivery.addressChange(new Address("city2", "street2", "@2222"));

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.BEFORE);
        assertThat(delivery.getAddress().getCity()).isEqualTo("city2");
    }

    @Test
    @DisplayName("배송 대기 상태가 아닐 때는 배송지 변경이 불가능하다")
    void cannotChangeAddress() {

        // given
        Delivery delivery = new Delivery(new Address("city1", "street1", "11111"));

        // when
        delivery.completeDelivery();

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.COMPLETE);
        org.junit.jupiter.api.Assertions
                .assertThrows(CannotChangeAddressException.class, () -> delivery.addressChange(new Address("city2", "street2", "@2222")));
    }

}