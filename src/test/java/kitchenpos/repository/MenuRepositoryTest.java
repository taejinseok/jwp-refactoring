package kitchenpos.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@DataJpaTest
@Sql("/data-for-dao.sql")
class MenuRepositoryTest {
    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("인자로 받는 ID 목록중 DB에 존재하는 Menu Id의 갯수를 반환한다.")
    @Test
    void countByIdIn() {
        long actual = menuRepository.countByIdIn(Arrays.asList(1L, 3L, 5L, 7L));
        assertThat(actual).isEqualTo(3L);
    }
}