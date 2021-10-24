package com.shoppingmall.domain.orders.service;

import com.shoppingmall.domain.delivery.Delivery;
import com.shoppingmall.domain.items.BasketItem;
import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.repository.BasketItemRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.OrderItem;
import com.shoppingmall.domain.orders.forms.AddressForm;
import com.shoppingmall.domain.orders.forms.OrderForm;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.domain.payment.Payment;
import com.shoppingmall.domain.payment.forms.PayForm;
import com.shoppingmall.domain.valuetype.Address;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final BasketItemRepository basketItemRepository;

    /**
     * 주문 메서드
     * @param memberId
     * @param BasketItemIds
     * @return
     */
    public Long order(Long memberId, List<Long> BasketItemIds, AddressForm form) {

        // 주문 멤버 생성
        Optional<Member> om = memberRepository.findById(memberId);
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));

        // 주문 상품 생성
        List<BasketItem> basketItems = new ArrayList<>();
        for (Long basketItemId : BasketItemIds) {
            Optional<BasketItem> ob = basketItemRepository.findBasketItemWithItemById(basketItemId);
            BasketItem basketItem = ob.orElseThrow(() -> new NoSuchElementException("장바구니에 존재하지 않는 상품입니다."));
            basketItems.add(basketItem);
        }

        List<OrderItem> orderItems = basketItems.stream()
                .map(bi -> OrderItem.createOrderItem(bi.getItem(), bi.getQuantity())).collect(Collectors.toList());

        // 배송 생성
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Delivery delivery = new Delivery(address);

        // 빈 결제 정보 생성
        Payment payment = new Payment();

        // 주문 생성, 저장
        Order order = Order.createOrder(member, delivery, payment, orderItems);
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 장바구니 담기 메서드
     * @param item
     * @param memberDto
     * @param qty
     */
    public void putInTheBasket(Item item, MemberDto memberDto, int qty) {
        Optional<Member> om = memberRepository.findById(memberDto.getId());
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원"));
        BasketItem basketItem = BasketItem.createBasketItem(item, member, qty);
        basketItemRepository.save(basketItem);
    }

    /**
     * 주문한 장바구니 상품들은 삭제
     * @param basketItemIds
     */
    public void deleteBasketItems(List<Long> basketItemIds) {
        basketItemRepository.deleteByIds(basketItemIds);
    }

    public void updatePayment(Long orderId, PayForm form) {

        Optional<Order> oo = orderRepository.findOrderWithPaymentByOrderId(orderId);
        Order order = oo.orElseThrow(() -> new IllegalStateException("존재하지 않는 주문"));
        order.pay(form.getPaidPrice(), form.getOption());
    }

    public void cancelOrder(Long orderId) {

        Optional<Order> oo = orderRepository.findOrderWithPaymentAndDeliveryByOrderId(orderId);
        Order order = oo.orElseThrow(() -> new IllegalStateException("존재하지 않는 주문"));
        order.cancelOrder();
    }

}
