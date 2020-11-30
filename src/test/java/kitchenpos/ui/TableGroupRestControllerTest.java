package kitchenpos.ui;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.repository.TableGroupRepository;
import kitchenpos.ui.dto.TableGroupCreateRequest;

@SuppressWarnings("NonAsciiCharacters")
class TableGroupRestControllerTest extends ControllerTest {

    @Autowired
    TableGroupRepository tableGroupRepository;

    @Autowired
    OrderTableRepository orderTableRepository;

    @Autowired
    OrderRepository orderRepository;

    @DisplayName("create: 2개 이상의 중복되지 않고 비어있지 않는 테이블목록에 대해 테이블 그룹 지정 요청시, 201 반환과 함께 그룹 지정된 테이블 그룹을 반환한다.")
    @Test
    void create() throws Exception {
        OrderTable 첫번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        OrderTable 두번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        TableGroupCreateRequest 지정하려는테이블그룹 = new TableGroupCreateRequest(Lists.list(첫번째빈테이블.getId(), 두번째빈테이블.getId()));

        String 테이블그룹추가_API_URL = "/api/table-groups";

        final ResultActions resultActions = create(테이블그룹추가_API_URL, 지정하려는테이블그룹);

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue(Long.class)))
                .andExpect(jsonPath("$.createdDate", notNullValue(LocalDateTime.class)))
                .andExpect(jsonPath("$.orderTables", hasSize(2)));
    }

    @DisplayName("ungroup: 테이블 그룹 대상에 포함되어있는 테이블 모두 주문 완료 상태인 경우, 해당 테이블들의 테이블 그룹 해지 후 204 코드를 반환한다.")
    @Test
    void ungroup() throws Exception {
        TableGroup 생성된테이블그룹 = tableGroupRepository.save(TableGroup.fromNow());
        OrderTable 첫번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        첫번째빈테이블.changeTableGroup(생성된테이블그룹);
        OrderTable 두번째빈테이블 = orderTableRepository.save(OrderTable.ofEmpty());
        두번째빈테이블.changeTableGroup(생성된테이블그룹);

        orderRepository.save(new Order(첫번째빈테이블, OrderStatus.COMPLETION, LocalDateTime.now()));
        orderRepository.save(new Order(두번째빈테이블, OrderStatus.COMPLETION, LocalDateTime.now()));

        String 테이블그룹해제_API_URL = "/api/table-groups/{tableGroupId}";

        final ResultActions resultActions = deleteByPathId(테이블그룹해제_API_URL, 생성된테이블그룹.getId());

        resultActions
                .andExpect(status().isNoContent());
    }
}