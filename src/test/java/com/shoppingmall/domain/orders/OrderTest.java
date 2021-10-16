package com.shoppingmall.domain.orders;

import com.shoppingmall.domain.delivery.Delivery;
import com.shoppingmall.domain.enums.DeliveryStatus;
import com.shoppingmall.domain.enums.OrderStatus;
import com.shoppingmall.domain.enums.PaymentOption;
import com.shoppingmall.domain.enums.PaymentStatus;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.OrderItem;
import com.shoppingmall.domain.payment.Payment;
import com.shoppingmall.domain.valuetype.Address;
import com.shoppingmall.exceptions.CannotCancelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class OrderTest {

    @Test
    @DisplayName("주문은 생성 메서드로 생성하며, 주문 상품 종류의 수와 총 주문 금액이 일치해야하고, 결제 전에 배송 상태는 BEFORE이다")
    void createOrder() {

        // given
        Member salesMan = Member.createMember("memberA", "aaa1111", "aaa#1111");
        salesMan.permitSale("abc1234");

        Pants jean1 = Pants.createPants("Jean1", 50000, 3, salesMan, 100, 28, 90);
        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, salesMan, 32, 71, 58);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 10, salesMan, 100, 3, 45);

        OrderItem orderItem1 = OrderItem.createOrderItem(jean1, jean1.getPrice(), 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(upper, upper.getPrice(), 2);
        OrderItem orderItem3 = OrderItem.createOrderItem(jacket1, jacket1.getPrice(), 1);

        Member customer = Member.createMember("memberB", "bbb111", "bbb1111@");

        Delivery delivery = new Delivery(new Address("city1", "street1", "1111"));
        Payment payment = new Payment();

        // when
        Order order = Order.createOrder(customer, delivery, payment, orderItem1, orderItem2, orderItem3);

        // then
        assertThat(order.getOrderItems().size()).isEqualTo(3);
        assertThat(order.getTotalOrderPrice()).isEqualTo(210000);
        assertThat(order.getDelivery().getStatus()).isEqualTo(DeliveryStatus.BEFORE);
    }

    @Test
    @DisplayName("주문 총액만큼 결제하면 지불완료 상태가 되면서 배송이 시작된다")
    void totalPriceAndPay() throws Exception {

        // given
        Member salesMan = Member.createMember("memberA", "aaa1111", "aaa#1111");
        salesMan.permitSale("abc1234");

        Pants jean1 = Pants.createPants("Jean1", 50000, 3, salesMan, 100, 28, 90);
        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, salesMan, 32, 71, 58);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 10, salesMan, 100, 3, 45);

        OrderItem orderItem1 = OrderItem.createOrderItem(jean1, jean1.getPrice(), 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(upper, upper.getPrice(), 2);
        OrderItem orderItem3 = OrderItem.createOrderItem(jacket1, jacket1.getPrice(), 1);

        Member customer = Member.createMember("memberB", "bbb111", "bbb1111@");

        Delivery delivery = new Delivery(new Address("city1", "street1", "1111"));
        Payment payment = new Payment();

        Order order = Order.createOrder(customer, delivery, payment, orderItem1, orderItem2, orderItem3);

        // when
        order.pay(PaymentOption.CREDIT_CARD);

        // then
        assertThat(order.getPayment().getPaidPrice()).isEqualTo(order.getTotalOrderPrice());
        assertThat(order.getDelivery().getStatus()).isEqualTo(DeliveryStatus.ING);
        assertThat(order.getPayment().getStatus()).isEqualTo(PaymentStatus.COMPLETE);
    }

    @Test
    @DisplayName("취소하면 배송 및 주문의 상태가 바뀐다")
    void cancel() throws Exception {

        // given
        Member salesMan = Member.createMember("memberA", "aaa1111", "aaa#1111");
        salesMan.permitSale("abc1234");

        Pants jean1 = Pants.createPants("Jean1", 50000, 3, salesMan, 100, 28, 90);
        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, salesMan, 32, 71, 58);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 10, salesMan, 100, 3, 45);

        OrderItem orderItem1 = OrderItem.createOrderItem(jean1, jean1.getPrice(), 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(upper, upper.getPrice(), 2);
        OrderItem orderItem3 = OrderItem.createOrderItem(jacket1, jacket1.getPrice(), 1);

        Member customer = Member.createMember("memberB", "bbb111", "bbb1111@");

        Delivery delivery = new Delivery(new Address("city1", "street1", "1111"));
        Payment payment = new Payment();

        Order order = Order.createOrder(customer, delivery, payment, orderItem1, orderItem2, orderItem3);

        // when
        order.cancelOrder();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(order.getDelivery().getStatus()).isEqualTo(DeliveryStatus.CANCEL);

    }

    @Test
    @DisplayName("이미 배송 완료되었다면 주문을 취소할 수 없다")
   void cannotCancel() throws Exception {

        // given
        Member salesMan = Member.createMember("memberA", "aaa1111", "aaa#1111");
        salesMan.permitSale("abc1234");

        Pants jean1 = Pants.createPants("Jean1", 50000, 3, salesMan, 100, 28, 90);
        Upper upper = Upper.createUpper("T-shirt1", 30000, 5, salesMan, 32, 71, 58);
        Outer jacket1 = Outer.createOuter("Jacket1", 100000, 10, salesMan, 100, 3, 45);

        OrderItem orderItem1 = OrderItem.createOrderItem(jean1, jean1.getPrice(), 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(upper, upper.getPrice(), 2);
        OrderItem orderItem3 = OrderItem.createOrderItem(jacket1, jacket1.getPrice(), 1);

        Member customer = Member.createMember("memberB", "bbb111", "bbb1111@");

        Delivery delivery = new Delivery(new Address("city1", "street1", "1111"));
        Payment payment = new Payment();

        Order order = Order.createOrder(customer, delivery, payment, orderItem1, orderItem2, orderItem3);
        order.pay(PaymentOption.CREDIT_CARD);

        // when
        order.getDelivery().completeDelivery(); // 배송 완료됐을 때

        // then
        org.junit.jupiter.api.Assertions.assertThrows(CannotCancelException.class, () -> order.cancelOrder());

    }

}
