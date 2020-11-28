package kitchenpos.ui.dto;

import java.util.List;
import java.util.stream.Collectors;

import kitchenpos.domain.OrderTable2;

public class OrderTableResponse {

    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableResponse(Long id, Long tableGroupId, int numberOfGuests, boolean empty) {
        this.id = id;
        this.tableGroupId = tableGroupId;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    private static OrderTableResponse of(OrderTable2 table) {
        return new OrderTableResponse(table.getId(), table.getTableGroupId(), table.getNumberOfGuests(),
                table.isEmpty());
    }

    public static List<OrderTableResponse> listOf(List<OrderTable2> tables) {
        return tables.stream()
                .map(OrderTableResponse::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public Long getTableGroupId() {
        return tableGroupId;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }
}
