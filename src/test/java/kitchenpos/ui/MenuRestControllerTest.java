package kitchenpos.ui;

import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.repository.MenuGroupRepository;
import kitchenpos.repository.ProductRepository;
import kitchenpos.ui.dto.MenuCreateRequest;
import kitchenpos.ui.dto.MenuProductRequest;

@SuppressWarnings("NonAsciiCharacters")
class MenuRestControllerTest extends ControllerTest {

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    ProductRepository productRepository;

    @DisplayName("create: 이름을 body message에 포함해 메뉴 등록을 요청시 ,메뉴 생성 성공 시 201 응답을 반환한다.")
    @Test
    void create() throws Exception {
        MenuGroup 세트메뉴 = menuGroupRepository.save(new MenuGroup("세트 그룹"));
        Product 후라이드치킨 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(20_000)));
        MenuProductRequest 후라이드치킨_두마리 = new MenuProductRequest(후라이드치킨.getId(), 2L);
        MenuCreateRequest 후라이드_2마리_세트_메뉴 = new MenuCreateRequest("후라이드 2마리 세트", BigDecimal.valueOf(40_000),
                세트메뉴.getId(), singletonList(후라이드치킨_두마리));

        final String 메뉴추가_API_URL = "/api/menus";
        final ResultActions resultActions = create(메뉴추가_API_URL, 후라이드_2마리_세트_메뉴);

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue(Long.class)))
                .andExpect(jsonPath("$.name", is("후라이드 2마리 세트")))
                .andExpect(jsonPath("$.price", is(40_000)))
                .andExpect(jsonPath("$.menuGroupId", notNullValue(Long.class)))
                .andExpect(jsonPath("$.menuProducts", hasSize(1)));
    }

    @DisplayName("list: 전체 메뉴 목록 요청시, 200 응답 코드와 함께 메뉴 목록을 반환한다.")
    @Test
    void list() throws Exception {
        final String 메뉴목록조회_API_URL = "/api/menus";
        final ResultActions resultActions = findList(메뉴목록조회_API_URL);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}