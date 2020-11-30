package kitchenpos.application;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
import kitchenpos.ui.dto.OrderResponse;
import kitchenpos.ui.dto.OrderStatusChangeRequest;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuProductRepository menuProductRepository;

    @DisplayName("list: 전체 주문 목록을 조회한다.")
    @Test
    void list() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));

        Product 후라이드단품 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(15_000)));
        MenuGroup 단품그룹 = menuGroupRepository.save(new MenuGroup("단품 그룹"));
        Menu 치킨단품메뉴 = menuRepository.save(new Menu("치킨 세트", BigDecimal.valueOf(15_000), 단품그룹));
        menuProductRepository.save(new MenuProduct(치킨단품메뉴, 후라이드단품, 1L));

        Order 새주문요청 = orderRepository.save(Order.ofCooking(점유중인테이블));
        orderLineItemRepository.save(new OrderLineItem(새주문요청, 치킨단품메뉴, 1L));

        final List<OrderResponse> 전체주문목록 = orderService.list();

        assertThat(전체주문목록).hasSize(1);
    }

    @DisplayName("create: 점유중인 테이블에서 메뉴 중복이 없는 하나 이상의 상품 주문시, 주문 추가 후, 생성된 주문 객체를 반환한다.")
    @Test
    void create() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));

        Product 후라이드단품 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(15_000)));
        MenuGroup 단품그룹 = menuGroupRepository.save(new MenuGroup("단품 그룹"));
        Menu 후라이드단품메뉴 = menuRepository.save(new Menu("치킨 세트", BigDecimal.valueOf(15_000), 단품그룹));
        menuProductRepository.save(new MenuProduct(후라이드단품메뉴, 후라이드단품, 1L));

        OrderCreateRequest 후라이드주문요청 = new OrderCreateRequest(점유중인테이블.getId(),
                asList(new OrderLineItemCreateRequest(후라이드단품메뉴.getId(), 1L)));
        OrderResponse 새주문 = orderService.create(후라이드주문요청);

        assertAll(
                () -> assertThat(새주문.getId()).isNotNull(),
                () -> assertThat(새주문.getOrderedTime()).isNotNull(),
                () -> assertThat(새주문.getOrderStatus()).isEqualTo("COOKING"),
                () -> assertThat(새주문.getOrderTableId()).isEqualTo(점유중인테이블.getId()),
                () -> assertThat(새주문.getOrderLineItems()).hasSize(1)
        );
    }

    @DisplayName("create: 점유중인 테이블에서 주문 상품이 없는 경우, 주문 추가 실패 후, IllegalArgumentException 발생")
    @Test
    void create_fail_if_order_contains_no_order_line_item() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        OrderCreateRequest 주문상품이없는주문요청 = new OrderCreateRequest(점유중인테이블.getId(), emptyList());

        assertThatThrownBy(() -> orderService.create(주문상품이없는주문요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 비어있는 테이블에서 주문 요청시, 주문 추가 실패 후, IllegalArgumentException 발생")
    @Test
    void create_fail_if_table_is_empty() {
        OrderTable 비어있는테이블 = orderTableRepository.save(OrderTable.ofEmpty());

        Product 후라이드단품 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(15_000)));
        MenuGroup 단품그룹 = menuGroupRepository.save(new MenuGroup("단품 그룹"));
        Menu 후라이드단품메뉴 = menuRepository.save(new Menu("치킨 세트", BigDecimal.valueOf(15_000), 단품그룹));
        menuProductRepository.save(new MenuProduct(후라이드단품메뉴, 후라이드단품, 1L));

        OrderCreateRequest 빈테이블에서주문요청 = new OrderCreateRequest(비어있는테이블.getId(),
                asList(new OrderLineItemCreateRequest(후라이드단품메뉴.getId(), 1L)));

        assertThatThrownBy(() -> orderService.create(빈테이블에서주문요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 점유중인 테이블에서 중복된 중복 메뉴를 포함한 상품 들 주문 요청시, 주문 추가 실패 후, IllegalArgumentException 발생")
    @Test
    void create_fail_if_order_line_item_contains_duplicate_menu() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));

        Product 후라이드단품 = productRepository.save(new Product("후라이드 치킨", BigDecimal.valueOf(15_000)));
        MenuGroup 단품그룹 = menuGroupRepository.save(new MenuGroup("단품 그룹"));
        Menu 후라이드단품메뉴 = menuRepository.save(new Menu("치킨 세트", BigDecimal.valueOf(15_000), 단품그룹));
        menuProductRepository.save(new MenuProduct(후라이드단품메뉴, 후라이드단품, 1L));

        OrderCreateRequest 중복메뉴가포함된주문요청 = new OrderCreateRequest(점유중인테이블.getId(),
                asList(new OrderLineItemCreateRequest(후라이드단품메뉴.getId(), 1L),
                        new OrderLineItemCreateRequest(후라이드단품메뉴.getId(), 2L)
                ));

        assertThatThrownBy(() -> orderService.create(중복메뉴가포함된주문요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("changeOrderStatus: 완료 되지 않는 주문의 경우, 주문 상태의 변경 요청시, 상태 변경 후, 변경된 주문 객체를 반환한다.")
    @Test
    void changeOrderStatus() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        Order 완료되지않은주문 = orderRepository.save(
                new Order(점유중인테이블, OrderStatus.COOKING,
                        LocalDateTime.of(2020, 10, 10, 20, 40)));

        Long 완료되지않은주문의식별자 = 완료되지않은주문.getId();
        OrderStatusChangeRequest 주문상태변경요청 = new OrderStatusChangeRequest(OrderStatus.MEAL.name());
        OrderResponse 상태변경완료된주문 = orderService.changeOrderStatus(완료되지않은주문의식별자, 주문상태변경요청);

        assertAll(
                () -> assertThat(상태변경완료된주문.getId()).isEqualTo(완료되지않은주문의식별자),
                () -> assertThat(상태변경완료된주문.getOrderedTime()).isNotNull(),
                () -> assertThat(상태변경완료된주문.getOrderStatus()).isEqualTo("MEAL"),
                () -> assertThat(상태변경완료된주문.getOrderTableId()).isEqualTo(점유중인테이블.getId())
        );
    }

    @DisplayName("changeOrderStatus: 이미 완료한 주문의 상태의 변경 요청시, 상태 변경 실패 후, IllegalArgumentException 발생.")
    @Test
    void changeOrderStatus_fail_if_order_status_is_completion() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        Order 완료상태의주문 = orderRepository.save(
                new Order(점유중인테이블, OrderStatus.COMPLETION,
                        LocalDateTime.of(2020, 10, 10, 20, 40)));
        Long 완료된주문의식별자 = 완료상태의주문.getId();
        OrderStatusChangeRequest 주문상태변경요청 = new OrderStatusChangeRequest(OrderStatus.MEAL.name());

        assertThatThrownBy(() -> orderService.changeOrderStatus(완료된주문의식별자, 주문상태변경요청))
                .isInstanceOf(IllegalArgumentException.class);
    }
}