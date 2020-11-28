package kitchenpos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.TableGroup2;

public interface TableGroupRepository extends JpaRepository<TableGroup2, Long> {
}
