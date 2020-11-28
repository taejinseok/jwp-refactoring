package kitchenpos.repository;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

@ActiveProfiles("test")
@DataJpaTest
@Sql("/data-for-dao.sql")
class MenuProductRepositoryTest {

    @Autowired
    private MenuProductRepository menuProductRepository;

    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("메뉴 ")
    @Test
    void findByMenuIn() {
        List<Menu> menus = menuRepository.findAllById(asList(1L, 2L, 3L));

        List<MenuProduct> byMenuIn = menuProductRepository.findByMenuIn(menus);
        assertThat(byMenuIn).hasSize(3);
    }
}