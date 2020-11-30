package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Lists.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.repository.TableGroupRepository;
import kitchenpos.ui.dto.TableGroupCreateRequest;
import kitchenpos.ui.dto.TableGroupResponse;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TableGroupServiceTest {

    @Autowired
    private TableGroupService tableGroupService;

    @Autowired
    private TableGroupRepository tableGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("create: 2개 이상의 중복 없이 존재, 비어있고, 그룹 지정 되지 않은 테이블 목록 그룹 지정시, 그룹 지정 후, 해당 객체를 반환한다.")
    @Test
    void create() {
        OrderTable 첫번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        OrderTable 두번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        TableGroupCreateRequest 지정하려는테이블그룹 = new TableGroupCreateRequest(list(첫번째빈테이블.getId(), 두번째빈테이블.getId()));

        TableGroupResponse 지정된테이블그룹 = tableGroupService.create(지정하려는테이블그룹);

        assertAll(
                () -> assertThat(지정된테이블그룹.getId()).isNotNull(),
                () -> assertThat(지정된테이블그룹.getCreatedDate()).isNotNull(),
                () -> assertThat(지정된테이블그룹.getOrderTables()).hasSize(2)
        );
    }

    @DisplayName("create: 1개 테이블 그룹 지정시, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_target_table_count_is_smaller_than_minimum_length() {
        OrderTable 첫번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        TableGroupCreateRequest 그룹지정대상이_1개인_그룹 = new TableGroupCreateRequest(list(첫번째빈테이블.getId()));

        assertThatThrownBy(() -> tableGroupService.create(그룹지정대상이_1개인_그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 그룹 지정 대상 테이블이 없는 경우, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_target_table_does_not_exist() {
        TableGroupCreateRequest 그룹지정대상이없는_테이블그룹 = new TableGroupCreateRequest(emptyList());

        assertThatThrownBy(() -> tableGroupService.create(그룹지정대상이없는_테이블그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 그룹 지정 대상 테이블이 null인 경우, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_target_table_is_null() {
        TableGroupCreateRequest 그룹지정_테이블이_null인_그룹 = new TableGroupCreateRequest(null);

        assertThatThrownBy(() -> tableGroupService.create(그룹지정_테이블이_null인_그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 그룹 지정 대상 테이블 들 중 중복인 테이블이 존재하는 경우, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_target_table_contains_duplicate_table() {
        OrderTable 비어있는테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        TableGroupCreateRequest 같은테이블을_두개이상포함하는_그룹 = new TableGroupCreateRequest(
                list(비어있는테이블.getId(), 비어있는테이블.getId()));

        assertThatThrownBy(() -> tableGroupService.create(같은테이블을_두개이상포함하는_그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 그룹 지정 대상 테이블 들 중 존재하지 않는 테이블이 포함된 경우, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_target_table_contains_non_exist_table() {
        OrderTable 비어있는테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        OrderTable 식별자없는테이블 = new OrderTable(0, true);
        TableGroupCreateRequest 식별자없는테이블을_포함하는_테이블그룹 = new TableGroupCreateRequest(
                list(비어있는테이블.getId(), 식별자없는테이블.getId())
        );

        assertThatThrownBy(() -> tableGroupService.create(식별자없는테이블을_포함하는_테이블그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 그룹 지정 대상 테이블 들 중 이미 점유중인 테이블이 포함된 경우, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_contains_non_empty_table() {
        OrderTable 비어있는테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        TableGroupCreateRequest 점유중인테이블을_포함하는_그룹 = new TableGroupCreateRequest(
                list(비어있는테이블.getId(), 점유중인테이블.getId())
        );

        assertThatThrownBy(() -> tableGroupService.create(점유중인테이블을_포함하는_그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("create: 그룹 지정 대상 테이블 들 중 이미 그룹 에 소속된 테이블 포함하는 경우, 그룹 지정 실패, IllegalArgumentException 발생한다.")
    @Test
    void create_fail_if_contains_already_grouped_table() {
        TableGroup 첫번째그룹 = tableGroupRepository.save(TableGroup.fromNow());
        OrderTable 첫번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        OrderTable 두번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        첫번째테이블.changeTableGroup(첫번째그룹);
        두번째테이블.changeTableGroup(첫번째그룹);

        OrderTable 세번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        TableGroupCreateRequest 다른그룹소속_테이블을_포함하는그룹 = new TableGroupCreateRequest(
                list(첫번째테이블.getId(), 세번째테이블.getId())
        );

        assertThatThrownBy(() -> tableGroupService.create(다른그룹소속_테이블을_포함하는그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("ungroup: 입력 받은 테이블 그룹 id에 대해 그룹 해제 대상 테이블 모두 주문 완료 상태인 경우, 대상 테이블들의 그룹화를 해제한다.")
    @Test
    void ungroup() {
        TableGroup 주문완료된그룹 = tableGroupRepository.save(TableGroup.fromNow());

        OrderTable 첫번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        첫번째테이블.changeTableGroup(주문완료된그룹);
        OrderTable 두번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        두번째테이블.changeTableGroup(주문완료된그룹);

        orderRepository.save(new Order(첫번째테이블, OrderStatus.COMPLETION, LocalDateTime.now()));
        orderRepository.save(new Order(두번째테이블, OrderStatus.COMPLETION, LocalDateTime.now()));

        tableGroupService.ungroup(주문완료된그룹.getId());

        assertAll(
                () -> assertThat(orderTableRepository.findById(첫번째테이블.getId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getTableGroupId()).isNull(),
                () -> assertThat(orderTableRepository.findById(두번째테이블.getId())
                        .orElseThrow(IllegalArgumentException::new)
                        .getTableGroupId()).isNull()
        );
    }

    @DisplayName("ungroup: 그룹 해제 대상 테이블 중 하나라도 주문 완료 상태가 아닌 경우, 그룹 해제 실패 및 IllegalArgumentException 발생.")
    @Test
    void ungroup_fail_if_contains_not_completion_order_status_table() {
        TableGroup 테이블그룹 = tableGroupRepository.save(TableGroup.fromNow());

        OrderTable 첫번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        첫번째테이블.changeTableGroup(테이블그룹);
        OrderTable 두번째테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        두번째테이블.changeTableGroup(테이블그룹);

        orderRepository.save(new Order(첫번째테이블, OrderStatus.MEAL, LocalDateTime.now()));
        orderRepository.save(new Order(두번째테이블, OrderStatus.COMPLETION, LocalDateTime.now()));

        assertThatThrownBy(() -> tableGroupService.ungroup(테이블그룹.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}