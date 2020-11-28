package kitchenpos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.Order2;

public interface OrderRepository extends JpaRepository<Order2, Long> {
}
