package kitchenpos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderTable_IdAndOrderStatusIn(Long tableId, List<OrderStatus> orderStatuses);

    boolean existsByOrderTable_IdInAndOrderStatusIn(List<Long> tableIds, List<OrderStatus> orderStatuses);
}
