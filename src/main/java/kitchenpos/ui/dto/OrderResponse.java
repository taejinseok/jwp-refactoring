package kitchenpos.ui.dto;

import java.time.LocalDateTime;
import java.util.List;

import kitchenpos.domain.Order2;

public class OrderResponse {

    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItemResponse> orderLineItems;

    private OrderResponse() {
    }

    private OrderResponse(Long id, Long orderTableId, String orderStatus, LocalDateTime orderedTime,
            List<OrderLineItemResponse> orderLineItems) {
        this.id = id;
        this.orderTableId = orderTableId;
        this.orderStatus = orderStatus;
        this.orderedTime = orderedTime;
        this.orderLineItems = orderLineItems;
    }

    public static OrderResponse of(Order2 order, List<OrderLineItemResponse> orderLineItemResponses) {
        return new OrderResponse(order.getId(), order.getOrderTableId(),
                order.getOrderStatus(), order.getOrderedTime(), orderLineItemResponses);
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public List<OrderLineItemResponse> getOrderLineItems() {
        return orderLineItems;
    }
}
