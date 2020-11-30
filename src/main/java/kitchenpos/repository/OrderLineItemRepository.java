package kitchenpos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {
    // List<OrderLineItem2> findByOrderIn(List<Order2> orders);
    List<OrderLineItem> findAllByOrderIn(List<Order> orders);

    List<OrderLineItem> findAllByOrder(Order order);
}
