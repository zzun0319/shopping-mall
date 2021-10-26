package com.shoppingmall.api.domains.delivery.controller;

import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.repository.OrderRepository;
import com.shoppingmall.domain.orders.service.OrderService;
import com.shoppingmall.exceptions.NoOrderExistException;
import com.shoppingmall.exceptions.WrongDeliveryStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryApiController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * 배송 상태 변경
     * @param orderId
     * @param deliveryStatus
     * @return
     */
    @PatchMapping("/{orderId}")
    public ResponseEntity<String> changeDeliveryStatus(@PathVariable("orderId") Long orderId,
                                                       @RequestBody String deliveryStatus) {

        Optional<Order> oo = orderRepository.findOrderWithDeliveryByOrderId(orderId);
        Order order = oo.orElseThrow(() -> new NoOrderExistException("존재하지 않는 주문"));

        try {
            orderService.changeDeliveryStatus(order, deliveryStatus);
        } catch (IllegalArgumentException e){
            throw new WrongDeliveryStatusException("배송 상태는 ING(배송중), COMPLETE(배송완료), CANCEL(배송취소)로 바꿀 수 있습니다.");
        }

        String result = "Delivery Status Changed";
        return ResponseEntity.ok(result);
    }
}
