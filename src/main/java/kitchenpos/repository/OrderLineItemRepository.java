package kitchenpos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.Order2;
import kitchenpos.domain.OrderLineItem2;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem2, Long> {
    // List<OrderLineItem2> findByOrderIn(List<Order2> orders);
    List<OrderLineItem2> findAllByOrderIn(List<Order2> orders);

    List<OrderLineItem2> findAllByOrder(Order2 order);
}
