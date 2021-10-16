package com.shoppingmall.domain.orders.service;

import com.shoppingmall.domain.delivery.Delivery;
import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.MemberRepository;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.OrderItem;
import com.shoppingmall.domain.orders.forms.OrderForm;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.domain.payment.Payment;
import com.shoppingmall.domain.valuetype.Address;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문 메서드
     * @param form
     * @return
     */
    @Transactional
    public Long order(OrderForm form) {

        // 회원 생성
        Optional<Member> om = memberRepository.findById(form.getMemberId());
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));

        // 상품 생성
        Optional<Item> optItem = itemRepository.findById(form.getItemId());
        Item item = optItem.orElseThrow(() -> new NoSuchItemException("상품이 없습니다."));

        // 배송 생성
        Delivery delivery = new Delivery(new Address(form.getCity(), form.getStreet(), form.getZipcode()));

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), form.getOrderQuantity());

        // 결제 정보 생성
        Payment payment = new Payment();

        // 주문 생성, 저장
        Order order = Order.createOrder(member, delivery, payment, orderItem);
        orderRepository.save(order);

        return order.getId();
    }

}
