package com.shoppingmall.api.domains.orders.dtos;

import com.shoppingmall.enums.DeliveryStatus;
import com.shoppingmall.enums.OrderStatus;
import com.shoppingmall.enums.PaymentStatus;
import com.shoppingmall.domain.orders.Order;
import com.shoppingmall.domain.orders.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderApiDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;
    private String orderItems;
    private Integer totalOrderPrice;

    public OrderApiDto(Order order) {
        orderId = order.getId();
        orderDate = order.getCreatedDate();
        orderStatus = order.getStatus();
        paymentStatus = order.getPayment().getStatus();
        deliveryStatus = order.getDelivery().getStatus();
        List<OrderItem> orderItems = order.getOrderItems();
        int left = orderItems.size() - 1;
        String repItemName = orderItems.get(0).getItem().getName();
        if(left == 0){
            this.orderItems = repItemName;
        } else {
            this.orderItems = repItemName + "외 " + left + "개의 상품";
        }
        totalOrderPrice = order.getTotalOrderPrice();
    }
}
