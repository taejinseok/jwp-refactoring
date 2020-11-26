package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kitchenpos.ui.dto.ProductCreateRequest;
import kitchenpos.ui.dto.ProductResponse;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @DisplayName("create: 이름과 가격을 입력 받아, 제품을 생성 요청 시, 입력 값을 기반으로  ID와 제품이 생성된다.")
    @Test
    void create() {
        ProductResponse 추가된새제품 = productService.create(new ProductCreateRequest("맛난 치킨", BigDecimal.valueOf(16_000)));

        assertAll(
                () -> assertThat(추가된새제품.getId()).isNotNull(),
                () -> assertThat(추가된새제품.getName()).isEqualTo("맛난 치킨"),
                () -> assertThat(추가된새제품.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(16_000))
        );
    }

    @DisplayName("create: 제품 생성 요청시, 입력 받은 가격이 음수라면, 제품 생성 시 예외가 발생한다.")
    @Test
    void create_throw_exception_if_price_is_negative() {
        ProductCreateRequest 추가된새제품 = new ProductCreateRequest("맛난 치킨", BigDecimal.valueOf(-16_000));

        assertThatThrownBy(() -> productService.create(추가된새제품))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("create: 제품 생성 요청시, 입력 받은 가격이 null이라면, 제품 생성 시 예외가 발생한다.")
    @Test
    void create_throw_exception_if_price_is_null() {
        ProductCreateRequest 가격이없는새제품_추가_요청 = new ProductCreateRequest("맛난 치킨", null);

        assertThatThrownBy(() -> productService.create(가격이없는새제품_추가_요청))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("list: 현재 저장 되어 있는 상품의 목록을 반환한다.")
    @Test
    void list() {
        productService.create(new ProductCreateRequest("후라이드 치킨", BigDecimal.valueOf(16_000)));
        productService.create(new ProductCreateRequest("오곡 치킨", BigDecimal.valueOf(16_000)));
        List<ProductResponse> 전체제품목록 = productService.list();

        assertThat(전체제품목록).hasSize(2);
    }
}