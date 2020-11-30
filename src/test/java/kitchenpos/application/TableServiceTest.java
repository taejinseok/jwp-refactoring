package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import kitchenpos.ui.dto.OrderTableChangeEmptyRequest;
import kitchenpos.ui.dto.OrderTableChangeGuestRequest;
import kitchenpos.ui.dto.OrderTableCreateRequest;
import kitchenpos.ui.dto.OrderTableResponse;
import kitchenpos.ui.dto.OrderTableWithTableGroupResponse;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TableServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableGroupRepository tableGroupRepository;

    @DisplayName("create: 테이블의 수용 인원, 빈 테이블 여부를 입력 받아, 새로운 테이블 entity를 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"true, 0", "false, 10"})
    void create(boolean isEmpty, int numberOfGuests) {
        OrderTableResponse 새테이블 = tableService.create(new OrderTableCreateRequest(numberOfGuests, isEmpty));

        assertAll(
                () -> assertThat(새테이블.getId()).isNotNull(),
                () -> assertThat(새테이블.getNumberOfGuests()).isEqualTo(numberOfGuests),
                () -> assertThat(새테이블.isEmpty()).isEqualTo(isEmpty)
        );
    }

    @DisplayName("list: 현재 저장 되어 있는 테이블의 전체 목록을 반환한다.")
    @Test
    void list() {
        tableService.create(new OrderTableCreateRequest(0, true));
        tableService.create(new OrderTableCreateRequest(0, true));
        tableService.create(new OrderTableCreateRequest(3, false));

        final List<OrderTableWithTableGroupResponse> 전체테이블목록 = tableService.list();

        assertThat(전체테이블목록).hasSize(3);
    }

    @DisplayName("changeEmpty: groupId가 없고, 현재 테이블의 주문이 완료 상태라면, 테이블의 비어있는 상태를 변경할 수 있다.")
    @Test
    void changeEmpty() {
        OrderTable 그루핑되지않는테이블 = orderTableRepository.save(new OrderTable(10, false));
        Long 테이블식별자 = 그루핑되지않는테이블.getId();
        Order 완료된주문 = new Order(그루핑되지않는테이블, OrderStatus.COMPLETION,
                LocalDateTime.of(2020, 8, 20, 20, 20));
        orderRepository.save(완료된주문);
        OrderTableChangeEmptyRequest 빈테이블 = new OrderTableChangeEmptyRequest(true);

        OrderTableResponse updatedOrderTable = tableService.changeEmpty(테이블식별자, 빈테이블);

        assertAll(
                () -> assertThat(updatedOrderTable.getId()).isNotNull(),
                () -> assertThat(updatedOrderTable.isEmpty()).isEqualTo(true),
                () -> assertThat(updatedOrderTable.getNumberOfGuests()).isEqualTo(10)
        );
    }

    @DisplayName("changeEmpty: 테이블 그룹이 지정된 테이블을 점유 상태 변경 시 IllegalArgumentException 발생한다")
    @Test
    void changeEmpty_fail_if_table_contains_group_table() {
        TableGroup 테이블그룹 = tableGroupRepository.save(
                new TableGroup(LocalDateTime.of(2020, 10, 15, 23, 50)));
        OrderTable 그루핑된테이블 = orderTableRepository.save(new OrderTable(테이블그룹, 10, false));

        Long 변경하려는테이블의식별자 = 그루핑된테이블.getId();
        assertThatThrownBy(() -> tableService.changeEmpty(변경하려는테이블의식별자, new OrderTableChangeEmptyRequest(true)))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("changeEmpty: 주문 완료 상태가 아닌 테이블 점유 상태 변경시, IllegalArgumentException 발생한다.")
    @Test
    void changeEmpty_fail_if_table_s_order_is_not_completion() {
        OrderTable 테이블 = orderTableRepository.save(new OrderTable(10, false));
        Long 테이블의식별자 = 테이블.getId();
        Order 테이블의_완료상태가아닌_주문 = orderRepository.save(
                new Order(테이블, OrderStatus.COOKING, LocalDateTime.of(2020, 8, 20, 20, 20)));

        assertThatThrownBy(() -> tableService.changeEmpty(테이블의식별자, new OrderTableChangeEmptyRequest(true)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("changeEmpty: 존재하지 않는 테이블 id의 테이블 점유 변경 요청 시, IllegalArgumentException 발생한다.")
    @Test
    void changeEmpty_fail_if_table_does_not_exist() {
        assertThatThrownBy(() -> tableService.changeEmpty(9999L, new OrderTableChangeEmptyRequest(true)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("changeNumberOfGuests: 비어있지 않는 테이블의 방문 인원 수 변경 시, 변경 이뤄진 테이블을 반환한다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        Long 점유중인테이블의식별자 = 점유중인테이블.getId();
        OrderTableChangeGuestRequest 인원수를변경하려는테이블 = new OrderTableChangeGuestRequest(10);

        OrderTableWithTableGroupResponse 인원수가변경된테이블 = tableService.changeNumberOfGuests(점유중인테이블의식별자, 인원수를변경하려는테이블);

        assertAll(
                () -> assertThat(인원수가변경된테이블.getId()).isNotNull(),
                () -> assertThat(인원수가변경된테이블.isEmpty()).isEqualTo(false),
                () -> assertThat(인원수가변경된테이블.getNumberOfGuests()).isEqualTo(10),
                () -> assertThat(인원수가변경된테이블.getTableGroupId()).isNull()
        );
    }

    @DisplayName("changeNumberOfGuests: 비어있지 않는 테이블의 방문 인원 수 변경 하려는 숫자가 음수라면, 변경에 실패하고, IllegalArgumentException을 발생한다.")
    @Test
    void changeNumberOfGuests_fail_if_number_of_guest_is_negative() {
        final OrderTable 점유중인테이블 = orderTableRepository.save(OrderTable.ofOccupied(5));
        final Long 점유중인테이블의식별자 = 점유중인테이블.getId();

        final OrderTableChangeGuestRequest 방문객의수가음수인테이블 = new OrderTableChangeGuestRequest(-10);

        assertThatThrownBy(() -> tableService.changeNumberOfGuests(점유중인테이블의식별자, 방문객의수가음수인테이블))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("changeNumberOfGuests: 비어있는 테이블의 방문 인원 수 변경시, 변경에 실패하고, IllegalArgumentException을 발생한다.")
    @Test
    void changeNumberOfGuests_fail_if_table_is_empty() {
        final OrderTable 비어있는테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        final Long savedTableId = 비어있는테이블.getId();

        final OrderTableChangeGuestRequest 변경인원수가포함된테이블 = new OrderTableChangeGuestRequest(10);

        assertThatThrownBy(() -> tableService.changeNumberOfGuests(savedTableId, 변경인원수가포함된테이블))
                .isInstanceOf(IllegalArgumentException.class);
    }
}