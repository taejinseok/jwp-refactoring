package kitchenpos.application;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.repository.MenuRepository;
import kitchenpos.repository.OrderLineItemRepository;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.ui.dto.OrderCreateRequest;
import kitchenpos.ui.dto.OrderLineItemCreateRequest;
import kitchenpos.ui.dto.OrderLineItemResponse;
import kitchenpos.ui.dto.OrderResponse;
import kitchenpos.ui.dto.OrderStatusChangeRequest;

@Service
public class OrderService {

    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderTableRepository orderTableRepository;

    public OrderService(
            final MenuRepository menuRepository,
            final OrderRepository orderRepository,
            final OrderLineItemRepository orderLineItemRepository,
            final OrderTableRepository orderTableRepository
    ) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public OrderResponse create(final OrderCreateRequest createRequest) {
        final List<OrderLineItemCreateRequest> orderLineItems = createRequest.getOrderLineItems();

        if (CollectionUtils.isEmpty(orderLineItems)) {
            throw new IllegalArgumentException();
        }

        final List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItemCreateRequest::getMenuId)
                .collect(Collectors.toList());

        List<Menu> menus = menuRepository.findAllById(menuIds);

        if (menus.size() != menuIds.size()) {
            throw new IllegalArgumentException();
        }

        final OrderTable orderTable = orderTableRepository.findById(createRequest.getOrderTableId())
                .orElseThrow(IllegalArgumentException::new);

        if (orderTable.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Order order = Order.ofCooking(orderTable);

        final Order savedOrder = orderRepository.save(order);

        List<OrderLineItem> orderLineItemList = orderLineItems.stream()
                .map(orderLineItemRequest -> {
                    Menu menu = findMenu(menus, orderLineItemRequest);
                    long quantity = orderLineItemRequest.getQuantity();
                    return new OrderLineItem(savedOrder, menu, quantity);
                })
                .collect(toList());

        List<OrderLineItemResponse> menuProductResponses = OrderLineItemResponse.listOf(
                orderLineItemRepository.saveAll(orderLineItemList));
        return OrderResponse.of(savedOrder, menuProductResponses);
    }

    private Menu findMenu(List<Menu> menus, OrderLineItemCreateRequest createRequest) {
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getId(), createRequest.getMenuId()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<OrderResponse> list() {
        final List<Order> orders = orderRepository.findAll();
        List<OrderLineItem> orderLineItems = orderLineItemRepository.findAllByOrderIn(orders);
        List<OrderResponse> result = new ArrayList<>();
        for (Order order : orders) {
            List<OrderLineItemResponse> collect = orderLineItems.stream()
                    .filter(orderLineItem -> Objects.equals(orderLineItem.getOrderId(), order.getId()))
                    .collect(collectingAndThen(toList(), OrderLineItemResponse::listOf));
            result.add(OrderResponse.of(order, collect));
        }

        return result;
    }

    @Transactional
    public OrderResponse changeOrderStatus(final Long orderId, final OrderStatusChangeRequest statusChangeRequest) {
        final Order savedOrder = orderRepository.findById(orderId)
                .orElseThrow(IllegalArgumentException::new);

        if (Objects.equals(OrderStatus.COMPLETION.name(), savedOrder.getOrderStatus())) {
            throw new IllegalArgumentException();
        }

        final OrderStatus orderStatus = OrderStatus.valueOf(statusChangeRequest.getOrderStatus());
        savedOrder.changeOrderStatus(orderStatus);

        List<OrderLineItem> orderLineItems = orderLineItemRepository.findAllByOrder(savedOrder);
        List<OrderLineItemResponse> orderLineItemResponses = OrderLineItemResponse.listOf(orderLineItems);

        return OrderResponse.of(savedOrder, orderLineItemResponses);
    }
}
