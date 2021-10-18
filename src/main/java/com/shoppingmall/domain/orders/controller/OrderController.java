package com.shoppingmall.domain.orders.controller;

import com.shoppingmall.domain.items.BasketItem;
import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.repository.BasketItemRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.forms.OrderForm;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.domain.orders.service.OrderService;
import com.shoppingmall.exceptions.CannotChangeAddressException;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final BasketItemRepository basketItemRepository;

    private final OrderService orderService;

    @GetMapping("/basket/{id}")
    public String basketList(@PathVariable("id") Member member, Model model){

        List<BasketItem> basketItems = basketItemRepository.findBasketItemsByMember(member);
        model.addAttribute("basketItems", basketItems);
        return "order/basket";
    }

    @PostMapping("/basket/{itemId}")
    public String putBasket(@PathVariable Long itemId, int qty, HttpSession session, RedirectAttributes ra){
        Optional<Item> oi = itemRepository.findById(itemId);
        Item item = oi.orElseThrow(() -> new NoSuchItemException("존재하지 않는 상품"));
        if(item.getStockQuantity() < qty){
            throw new CannotChangeAddressException("재고보다 많은 상품을 주문했습니다.");
        }

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        ra.addAttribute("id", loginMember.getId());

        orderService.putInTheBasket(item, loginMember, qty);

        return "redirect:/orders/basket/{id}";
    }

    @GetMapping("/{memberId}")
    public String orderList(@PathVariable Long memberId, Model model) {
        Optional<Member> om = memberRepository.findById(memberId);
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));
        List<Order> orders = orderRepository.findOrdersByMember(member);
        // orderDto로 변환. model에 담기
        return "order/order-list";
    }

    @PostMapping
    public String order(@Validated @ModelAttribute("form") OrderForm form, RedirectAttributes ra){
        orderService.order(form);
        ra.addAttribute("memberId", form.getMemberId());
        return "redirect:/orders/{memberId}";
    }

}
