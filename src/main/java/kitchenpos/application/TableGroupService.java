package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.repository.TableGroupRepository;
import kitchenpos.ui.dto.OrderTableResponse;
import kitchenpos.ui.dto.TableGroupCreateRequest;
import kitchenpos.ui.dto.TableGroupResponse;

@Service
public class TableGroupService {

    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableGroupService(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository,
            final TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
    }

    @Transactional
    public TableGroupResponse create(final TableGroupCreateRequest createRequest) {
        List<Long> orderTableIds = createRequest.getOrderTableIds();

        if (Objects.isNull(orderTableIds)) {
            throw new IllegalArgumentException();
        }

        final List<OrderTable> savedOrderTables = orderTableRepository.findAllById(orderTableIds);

        if (CollectionUtils.isEmpty(savedOrderTables) || savedOrderTables.size() < 2) {
            throw new IllegalArgumentException();
        }

        if (orderTableIds.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException();
        }

        for (final OrderTable savedOrderTable : savedOrderTables) {
            if (!savedOrderTable.isEmpty() || Objects.nonNull(savedOrderTable.getTableGroupId())) {
                throw new IllegalArgumentException();
            }
        }

        TableGroup savedTableGroup = tableGroupRepository.save(TableGroup.fromNow());
        for (OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.changeTableGroup(savedTableGroup);
        }

        List<OrderTableResponse> orderTableResponses = OrderTableResponse.listOf(savedOrderTables);
        return TableGroupResponse.of(savedTableGroup, orderTableResponses);
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        final List<OrderTable> orderTables = orderTableRepository.findAllByTableGroup_Id(tableGroupId);

        final List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        if (orderRepository.existsByOrderTable_IdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL))) {
            throw new IllegalArgumentException();
        }

        for (final OrderTable orderTable : orderTables) {
            orderTable.changeTableGroup(null);
        }
    }
}
