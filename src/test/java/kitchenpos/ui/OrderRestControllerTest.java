package kitchenpos.ui;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.repository.MenuGroupRepository;
import kitchenpos.repository.MenuProductRepository;
import kitchenpos.repository.MenuRepository;
import kitchenpos.repository.OrderLineItemRepository;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.repository.ProductRepository;
import kitchenpos.ui.dto.OrderCreateRequest;
import kitchenpos.ui.dto.OrderLineItemCreateRequest;
import kitchenpos.ui.dto.OrderStatusChangeRequest;

@SuppressWarnings("NonAsciiCharacters")
class OrderRestControllerTest extends ControllerTest {

    @Autowired
    OrderTableRepository orderTableRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuProductRepository menuProductRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderLineItemRepository orderLineItemRepository;

    @DisplayName("create: 테이블, 주문 라인 목록과 함께 주문 추가 요청을 한다. 새 주문 생성 성공 후 201 응답을 반환한다.")
    @Test
    void create() throws Exception {
        OrderTable 점유중인테이블 = orderTableRepository.save(new OrderTable(5, false));
        Product 후라이드단품 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(15_000)));
        MenuGroup 단품메뉴그룹 = menuGroupRepository.save(new MenuGroup("단품 메뉴"));
        Menu 후라이드한마리세트 = menuRepository.save(new Menu("치킨 세트", BigDecimal.valueOf(15_000), 단품메뉴그룹));
        menuProductRepository.save(new MenuProduct(후라이드한마리세트, 후라이드단품, 1L));

        OrderLineItemCreateRequest 주문항목 = new OrderLineItemCreateRequest(후라이드한마리세트.getId(), 1);
        OrderCreateRequest 생성하려는주문 = new OrderCreateRequest(점유중인테이블.getId(), Lists.list(주문항목));

        final String 주문추가_API_URL = "/api/orders";
        final ResultActions resultActions = create(주문추가_API_URL, 생성하려는주문);

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue(Long.class)))
                .andExpect(jsonPath("$.orderTableId", notNullValue(Long.class)))
                .andExpect(jsonPath("$.orderStatus", is("COOKING")))
                .andExpect(jsonPath("$.orderedTime", notNullValue(LocalDateTime.class)))
                .andExpect(jsonPath("$.orderLineItems", hasSize(1)));
    }

    @DisplayName("list: 전체 주문 목록 조회 요청시, 200 상태 코드와 함께, 전체 주문 내역을 반환한다.")
    @Test
    void list() throws Exception {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        Product 후라이드단품 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(15_000)));
        MenuGroup 단품메뉴그룹 = menuGroupRepository.save(new MenuGroup("단품 그룹"));
        Menu 후라이드한마리세트 = menuRepository.save(new Menu("치킨 세트", BigDecimal.valueOf(15_000), 단품메뉴그룹));
        menuProductRepository.save(new MenuProduct(후라이드한마리세트, 후라이드단품, 1L));

        Order 생성하려는주문 = orderRepository.save(Order.ofCooking(점유중인테이블));
        OrderLineItem 주문항목 = orderLineItemRepository.save(new OrderLineItem(생성하려는주문, 후라이드한마리세트, 1));

        final String 주문목록조회_API_URL = "/api/orders";
        final ResultActions resultActions = findList(주문목록조회_API_URL);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @DisplayName("changeOrderStatus: 요리 완료 상태가 아닌 경우, 주문 현재 진행 상태 변경 요청시 변경 후, 200 상태코드와, 변경한 주문 내용을 반환한다.")
    @Test
    void changeOrderStatus() throws Exception {
        OrderTable 점유중인테이블 = orderTableRepository.save(new OrderTable(5, false));
        Order 완료되지않은주문 = orderRepository.save(Order.ofCooking(점유중인테이블));

        Long 기존의주문Id = 완료되지않은주문.getId();
        OrderStatusChangeRequest 변경하려는주문내용 = new OrderStatusChangeRequest(OrderStatus.MEAL.name());

        String 주문상태변경_API_URL = "/api/orders/{orderId}/order-status";
        final ResultActions resultActions = updateByPathIdAndBody(주문상태변경_API_URL, 기존의주문Id, 변경하려는주문내용);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(Long.class)))
                .andExpect(jsonPath("$.orderTableId", notNullValue(Long.class)))
                .andExpect(jsonPath("$.orderStatus", is("MEAL")))
                .andExpect(jsonPath("$.orderedTime", notNullValue(LocalDateTime.class)));

    }
}