package kitchenpos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.OrderTable2;

public interface OrderTableRepository extends JpaRepository<OrderTable2, Long> {
}
