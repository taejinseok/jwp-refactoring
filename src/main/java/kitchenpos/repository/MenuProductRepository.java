package kitchenpos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kitchenpos.domain.MenuProduct;

public interface MenuProductRepository extends JpaRepository<MenuProduct, Long> {
    @Query("select mp from MenuProduct mp where mp.menuId = :menuId")
    List<MenuProduct> findAllByMenuId(@Param("menuId") Long menuId);
}
