package com.shoppingmall.domain.orders.dtos;

import com.shoppingmall.enums.DeliveryStatus;
import com.shoppingmall.enums.OrderStatus;
import com.shoppingmall.enums.PaymentStatus;
import com.shoppingmall.domain.orders.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;
    private List<OrderItemDto> orderItems;
    private Integer totalOrderPrice;

    public OrderDto(Order order) {
        orderId = order.getId();
        orderDate = order.getCreatedDate();
        orderStatus = order.getStatus();
        paymentStatus = order.getPayment().getStatus();
        deliveryStatus = order.getDelivery().getStatus();
        orderItems = order.getOrderItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
        totalOrderPrice = order.getTotalOrderPrice();
    }
}
