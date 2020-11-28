package kitchenpos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.Order2;
import kitchenpos.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<Order2, Long> {
    boolean existsByOrderTable_IdAndOrderStatusIn(Long tableId, List<OrderStatus> orderStatuses);
}
