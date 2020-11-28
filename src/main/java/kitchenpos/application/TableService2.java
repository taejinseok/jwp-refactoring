package kitchenpos.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.domain.OrderTable2;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.ui.dto.OrderTableCreateRequest;
import kitchenpos.ui.dto.OrderTableCreateResponse;
import kitchenpos.ui.dto.OrderTableResponse;

@Service
public class TableService2 {
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;

    public TableService2(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public OrderTableCreateResponse create(final OrderTableCreateRequest createRequest) {
        OrderTable2 savedTable = orderTableRepository.save(createRequest.toEntity());
        return OrderTableCreateResponse.of(savedTable);
    }

    public List<OrderTableResponse> list() {
        List<OrderTable2> tables = orderTableRepository.findAll();
        return OrderTableResponse.listOf(tables);
    }

    // @Transactional
    // public OrderTable2 changeEmpty(final Long orderTableId, final OrderTable2 orderTable) {
    //     final OrderTable2 savedOrderTable = orderTableRepository.findById(orderTableId)
    //             .orElseThrow(IllegalArgumentException::new);
    //
    //     if (Objects.nonNull(savedOrderTable.getTableGroupId())) {
    //         throw new IllegalArgumentException();
    //     }
    //
    //     if (orderRepository.existsByOrderTableIdAndOrderStatusIn(
    //             orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
    //         throw new IllegalArgumentException();
    //     }
    //
    //     savedOrderTable.setEmpty(orderTable.isEmpty());
    //
    //     return orderTableRepository.save(savedOrderTable);
    // }
    //
    // @Transactional
    // public OrderTable2 changeNumberOfGuests(final Long orderTableId, final OrderTable2 orderTable) {
    //     final int numberOfGuests = orderTable.getNumberOfGuests();
    //
    //     if (numberOfGuests < 0) {
    //         throw new IllegalArgumentException();
    //     }
    //
    //     final OrderTable2 savedOrderTable = orderTableRepository.findById(orderTableId)
    //             .orElseThrow(IllegalArgumentException::new);
    //
    //     if (savedOrderTable.isEmpty()) {
    //         throw new IllegalArgumentException();
    //     }
    //
    //     savedOrderTable.setNumberOfGuests(numberOfGuests);
    //
    //     return orderTableRepository.save(savedOrderTable);
    // }
}
