package kitchenpos.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@DataJpaTest
class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("인자로 받는 ID 목록중 DB에 존재하는 Menu Id의 갯수를 반환한다.")
    @Test
    void countByIdIn() {
        MenuGroup 첫번째메뉴그룹 = menuGroupRepository.save(new MenuGroup("1번그룹"));
        Menu 첫번째메뉴 = menuRepository.save(new Menu("1번메뉴", BigDecimal.valueOf(10_000), 첫번째메뉴그룹));
        Menu 두번째메뉴 = menuRepository.save(new Menu("1번메뉴", BigDecimal.valueOf(10_000), 첫번째메뉴그룹));
        Menu 세번째메뉴 = menuRepository.save(new Menu("1번메뉴", BigDecimal.valueOf(10_000), 첫번째메뉴그룹));

        long actual = menuRepository.countByIdIn(Arrays.asList(첫번째메뉴.getId(), 두번째메뉴.getId(), 세번째메뉴.getId()));
        assertThat(actual).isEqualTo(3L);
    }
}