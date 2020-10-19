package kitchenpos.ui;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import kitchenpos.domain.Product;

class ProductRestControllerTest extends ControllerTest {
    private static final String PRODUCT_API_URL = "/api/products";

    @DisplayName("create: 이름, 가격을 body message에 포함해 제품 등록을 요청시 , 요청값을 바탕으로 제품을 생성 후 생성 성공 시 201 응답을 반환한다.")
    @Test
    void create() throws Exception {
        final Product product = new Product();
        product.setName("치킨");
        product.setPrice(BigDecimal.valueOf(16_000));

        final ResultActions resultActions = create(PRODUCT_API_URL, product);

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("치킨")))
                .andExpect(jsonPath("$.price", is(16_000d)));

    }

    @DisplayName("list: 제품 목록 요청시, 등록된 제품의 목록을 body message로 가지고 있는 status code 200 응답을 반환한다.")
    @Test
    void list() throws Exception {
        final ResultActions resultActions = findList(PRODUCT_API_URL);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }
}