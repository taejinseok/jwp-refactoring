package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable2;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.ui.dto.OrderTableChangeEmptyRequest;
import kitchenpos.ui.dto.OrderTableChangeGuestRequest;
import kitchenpos.ui.dto.OrderTableCreateRequest;
import kitchenpos.ui.dto.OrderTableResponse;
import kitchenpos.ui.dto.OrderTableWithTableGroupResponse;

@Service
public class TableService {

    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;

    public TableService(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public OrderTableResponse create(final OrderTableCreateRequest createRequest) {
        OrderTable2 savedTable = orderTableRepository.save(createRequest.toEntity());
        return OrderTableResponse.of(savedTable);
    }

    public List<OrderTableWithTableGroupResponse> list() {
        List<OrderTable2> tables = orderTableRepository.findAll();
        return OrderTableWithTableGroupResponse.listOf(tables);
    }

    @Transactional
    public OrderTableResponse changeEmpty(final Long orderTableId, final OrderTableChangeEmptyRequest request) {
        final OrderTable2 savedOrderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(IllegalArgumentException::new);

        if (Objects.nonNull(savedOrderTable.getTableGroupId())) {
            throw new IllegalArgumentException();
        }

        if (orderRepository.existsByOrderTable_IdAndOrderStatusIn(
                orderTableId, Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL))) {
            throw new IllegalArgumentException();
        }

        savedOrderTable.changeEmptyStatus(request.isEmpty());

        return OrderTableResponse.of(savedOrderTable);
    }

    @Transactional
    public OrderTableWithTableGroupResponse changeNumberOfGuests(final Long orderTableId,
            final OrderTableChangeGuestRequest request) {
        final int numberOfGuests = request.getNumberOfGuests();

        if (numberOfGuests < 0) {
            throw new IllegalArgumentException();
        }

        final OrderTable2 savedOrderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(IllegalArgumentException::new);

        if (savedOrderTable.isEmpty()) {
            throw new IllegalArgumentException();
        }

        savedOrderTable.changeNumberOfGuests(numberOfGuests);

        return OrderTableWithTableGroupResponse.of(savedOrderTable);
    }
}
