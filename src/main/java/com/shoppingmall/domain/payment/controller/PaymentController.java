package com.shoppingmall.domain.payment.controller;

import com.shoppingmall.enums.PaymentOption;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.domain.orders.service.OrderService;
import com.shoppingmall.domain.payment.forms.PayForm;
import com.shoppingmall.exceptions.NoSuchOrderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * 결제 페이지로 이동
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping("/{orderId}")
    public String payPage(@PathVariable("orderId") Long orderId, Model model){

        addModelTotalPriceAndOptions(orderId, model);
        model.addAttribute("form", new PayForm());
        return "pay/pay";
    }

    /**
     * 결제
     * @param orderId
     * @param session
     * @param form
     * @param bindingResult
     * @param ra
     * @return
     */
    @PostMapping("/{orderId}")
    public String pay(@PathVariable("orderId") Long orderId, HttpSession session,
                      @Validated @ModelAttribute("form") PayForm form, BindingResult bindingResult,
                      RedirectAttributes ra, Model model) {

        log.info("결제해야할 금액: {}, 결제한 금액: {}", form.getTotalPrice(), form.getPaidPrice());
        if (!form.getTotalPrice().equals(form.getPaidPrice())) {
            bindingResult.rejectValue("paidPrice", "different");
        }

        if(bindingResult.hasErrors()){
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.info(error.getCode());
            }
            addModelTotalPriceAndOptions(orderId, model);
            return "pay/pay";
        }

        orderService.updatePayment(orderId, form);

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        ra.addAttribute("memberId", loginMember.getId());
        return "redirect:/orders/{memberId}";
    }

    /**
     * 주문 총액, 결제 수단들 리스트를 모델에 담아주는 메서드
     * @param orderId
     * @param model
     */
    private void addModelTotalPriceAndOptions(@PathVariable("orderId") Long orderId, Model model) {
        Optional<Order> oo = orderRepository.findById(orderId);
        Order findOrder = oo.orElseThrow(() -> new NoSuchOrderException("존재하지 않는 주문"));

        // 결제 옵션
        model.addAttribute("options", PaymentOption.values());

        model.addAttribute("totalPrice", findOrder.getTotalOrderPrice());
    }
}
