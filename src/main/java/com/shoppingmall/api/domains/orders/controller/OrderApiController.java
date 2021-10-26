package com.shoppingmall.api.domains.orders.controller;

import com.shoppingmall.api.domains.orders.dtos.OrderApiDto;
import com.shoppingmall.enums.DeliveryStatus;
import com.shoppingmall.enums.PaymentStatus;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.exceptions.WrongStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 주문 목록 보기
     * @param payStatus
     * @param deliStatus
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity getOrders(@RequestParam(value = "payStatus", required = false) String payStatus, @RequestParam(value = "deliStatus", required = false) String deliStatus,
                                    @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable){

        PaymentStatus p_status = null;
        DeliveryStatus d_status = null;
        log.info("주문 조회, {} {}", payStatus, deliStatus);

        try {
            p_status = PaymentStatus.valueOf(payStatus);
            d_status = DeliveryStatus.valueOf(deliStatus);
        } catch (IllegalArgumentException e){
            throw new WrongStatusException(
                    "결제상태(payStatus)는 COMPLETE, NOT_YET 중 하나로 / 배송상태(deliStatus)는 COMPLETE, ING, BEFORE, CANCEL 중 하나로");
        }
        catch (NullPointerException ne){
            throw new WrongStatusException("payStatus와 deliStatus를 요청 파라미터로 넘겨주세요.");
        }

        Page<Order> ordersWithDelAndPay = orderRepository.findOrdersWithDelAndPay(d_status, p_status, pageable);
        Page<OrderApiDto> page = ordersWithDelAndPay.map(OrderApiDto::new);
        return ResponseEntity.ok().body(page);
    }

    // TODO, 주문하기
    // TODO 주문 취소

    // 상품 이미지를 보고 다 고려해서 장바구니에 담는 것이 자연스러우므로 아직 이미지가 깨져보이는 것만 알기 때문에, 뷰에서 담는 것이 자연스럽다.
    // 장바구니에서 주문으로 넘어가도록 만들었고, 그게 자연스러우므로 주문도 뷰로
}
