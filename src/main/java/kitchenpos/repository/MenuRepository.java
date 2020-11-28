package kitchenpos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Long countByIdIn(List<Long> ids);
}
