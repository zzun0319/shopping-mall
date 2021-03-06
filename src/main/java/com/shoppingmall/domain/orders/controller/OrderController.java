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
import com.shoppingmall.exceptions.NotEnoughStockException;
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
     * ???????????? ?????? ????????????
     * @param member
     * @param model
     * @return
     */
    @GetMapping("/basket/{id}")
    public String basketList(@PathVariable("id") Member member, Model model){

        model.addAttribute("form", new AddressForm());

        List<BasketItem> basketItems = basketItemRepository.findBasketItemsByMember(member);
        basketItemService.allUpdate(); // ????????? ??? ????????? ????????????
        model.addAttribute("basketItems", basketItems);
        return "order/basket";
    }

    /**
     * ???????????? ??????
     * @param itemId
     * @param qty
     * @param loginMember
     * @param ra
     * @return
     */
    @PostMapping("/basket/{itemId}")
    public String putBasket(@PathVariable Long itemId, int qty, @SessionAttribute(name = "loginMember", required = false) MemberDto loginMember, RedirectAttributes ra){
        Optional<Item> oi = itemRepository.findById(itemId);
        Item item = oi.orElseThrow(() -> new NoSuchItemException("???????????? ?????? ??????"));
        if(item.getStockQuantity() < qty){
            throw new NotEnoughStockException("???????????? ?????? ????????? ??????????????????.");
        }

        if (loginMember != null) {
            ra.addAttribute("id", loginMember.getId());
        }

        orderService.putInTheBasket(item, loginMember, qty);

        return "redirect:/orders/basket/{id}";
    }

    /**
     * ?????? ?????? ??????
     * @param memberId
     * @param model
     * @return
     */
    @GetMapping("/{memberId}")
    public String orderList(@PathVariable Long memberId, Model model, Pageable pageable) {
        Optional<Member> om = memberRepository.findById(memberId);
        Member member = om.orElseThrow(() -> new NoSuchMemberException("???????????? ?????? ???????????????."));
        Page<Order> orders = orderRepository.findOrdersDeliveryPaymentByMember(member, pageable);

        // orderDto??? ??????. model??? ??????
        Page<OrderDto> orderDtos = orders.map(OrderDto::new);
        model.addAttribute("orders", orderDtos);
        return "order/order-list";
    }

    /**
     * ????????????
     * @param request
     * @param ra
     * @return
     */
    @PostMapping
    public String order(@Validated @ModelAttribute("form") AddressForm form, BindingResult bindingResult,
                        HttpServletRequest request, @SessionAttribute(name = "loginMember", required = false) MemberDto loginMember, RedirectAttributes ra, Model model){

        String[] selectedNumberString = request.getParameterValues("select");

        if(loginMember != null && bindingResult.hasErrors()){
            validate(model, loginMember);
            return "order/basket";
        }

        if(loginMember != null && selectedNumberString == null){
            validate(model, loginMember);
            model.addAttribute("msg", "????????? ????????? ??????????????????.");
            return "order/basket";
        }

        List<Long> BasketItemIds = Arrays.stream(selectedNumberString).map(s -> Long.parseLong(s)).collect(Collectors.toList());

        
        Long orderId = null;
        if (loginMember != null) {
            orderId = orderService.order(loginMember.getId(), BasketItemIds, form);
        }
        ra.addAttribute("orderId", orderId);
        
        // ????????? ?????? ????????? ??????????????? ????????????????????? ??????
        orderService.deleteBasketItems(BasketItemIds);
        return "redirect:/payment/{orderId}";
    }

    /**
     * ?????? ??????
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
     * RedirectAttribute??? ?????? ????????? ?????? ????????? id??? ????????????.
     * @param session
     * @param ra
     */
    private void AddRedirectAttributeMemberId(HttpSession session, RedirectAttributes ra) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if(loginMember != null){
            ra.addAttribute("memberId", loginMember.getId());
        }
    }

    /**
     * ????????? ????????? ???????????? ????????????, ??????????????? ????????? ?????? ??? ?????? ?????? ????????? ???????????? ???????????? ????????? ???????????? ?????????
     * @param model
     * @param loginMember
     */
    private void validate(Model model, MemberDto loginMember) {

        Optional<Member> om = memberRepository.findById(loginMember.getId());
        Member member = om.orElseThrow(() -> new NoSuchMemberException("???????????? ?????? ??????"));

        List<BasketItem> basketItems = basketItemRepository.findBasketItemsByMember(member);
        basketItemService.allUpdate(); // ????????? ??? ????????? ????????????
        model.addAttribute("basketItems", basketItems);
    }

}
