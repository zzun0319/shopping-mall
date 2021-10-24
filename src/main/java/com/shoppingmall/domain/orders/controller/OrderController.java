package com.shoppingmall.domain.orders.controller;

import com.shoppingmall.domain.items.BasketItem;
import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.repository.BasketItemRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.items.service.BasketItemService;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.dtos.OrderDto;
import com.shoppingmall.domain.orders.forms.AddressForm;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.domain.orders.service.OrderService;
import com.shoppingmall.exceptions.CannotChangeAddressException;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final BasketItemService basketItemService;

    /**
     * 장바구니 목록 보여주기
     * @param member
     * @param model
     * @return
     */
    @GetMapping("/basket/{id}")
    public String basketList(@PathVariable("id") Member member, Model model){

        model.addAttribute("form", new AddressForm());

        List<BasketItem> basketItems = basketItemRepository.findBasketItemsByMember(member);
        basketItemService.allUpdate(); // 품절된 거 있는지 업데이트
        model.addAttribute("basketItems", basketItems);
        return "order/basket";
    }

    /**
     * 장바구니 담기
     * @param itemId
     * @param qty
     * @param session
     * @param ra
     * @return
     */
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

    /**
     * 주문 목록 보기
     * @param memberId
     * @param model
     * @return
     */
    @GetMapping("/{memberId}")
    public String orderList(@PathVariable Long memberId, Model model, Pageable pageable) {
        Optional<Member> om = memberRepository.findById(memberId);
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));
        Page<Order> orders = orderRepository.findOrdersDeliveryPaymentByMember(member, pageable);

        // orderDto로 변환. model에 담기
        Page<OrderDto> orderDtos = orders.map(OrderDto::new);
        model.addAttribute("orders", orderDtos);
        return "order/order-list";
    }

    /**
     * 주문하기
     * @param request
     * @param ra
     * @return
     */
    @PostMapping
    public String order(@Validated @ModelAttribute("form") AddressForm form, BindingResult bindingResult,
                        HttpServletRequest request, HttpSession session, RedirectAttributes ra, Model model){

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        String[] selectedNumberString = request.getParameterValues("select");

        if(bindingResult.hasErrors()){
            validate(model, loginMember);
            return "order/basket";
        }

        if(selectedNumberString == null){
            validate(model, loginMember);
            model.addAttribute("msg", "주문할 상품을 선택해주세요.");
            return "order/basket";
        }

        List<Long> BasketItemIds = Arrays.stream(selectedNumberString).map(s -> Long.parseLong(s)).collect(Collectors.toList());

        
        Long orderId = orderService.order(loginMember.getId(), BasketItemIds, form);
        ra.addAttribute("orderId", orderId);
        
        // 주문이 이미 들어간 상태이므로 장바구니에서는 삭제
        orderService.deleteBasketItems(BasketItemIds);
        return "redirect:/payment/{orderId}";
    }

    /**
     * 주문 취소
     * @param orderId
     * @param session
     * @param ra
     * @return
     */
    @PostMapping("/{orderId}/cancel")
    public String orderCancel(@PathVariable("orderId") Long orderId, HttpSession session, RedirectAttributes ra){

        orderService.cancelOrder(orderId);

        AddRedirectAttributeMemberId(session, ra);
        return "redirect:/orders/{memberId}";
    }

    /**
     * RedirectAttribute에 현재 로그인 중인 회원의 id를 넣어준다.
     * @param session
     * @param ra
     */
    private void AddRedirectAttributeMemberId(HttpSession session, RedirectAttributes ra) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        ra.addAttribute("memberId", loginMember.getId());
    }

    /**
     * 주문할 상품을 선택하지 않았거나, 입력값들에 문제가 있을 때 다시 해당 회원의 장바구니 리스트를 모델에 담아주는 메서드
     * @param model
     * @param loginMember
     */
    private void validate(Model model, MemberDto loginMember) {

        Optional<Member> om = memberRepository.findById(loginMember.getId());
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원"));

        List<BasketItem> basketItems = basketItemRepository.findBasketItemsByMember(member);
        basketItemService.allUpdate(); // 품절된 거 있는지 업데이트
        model.addAttribute("basketItems", basketItems);
    }

}
