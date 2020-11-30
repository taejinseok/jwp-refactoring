package kitchenpos.ui.dto;

import java.util.List;
import java.util.stream.Collectors;

import kitchenpos.domain.OrderTable;

public class OrderTableWithTableGroupResponse {

    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableWithTableGroupResponse(Long id, Long tableGroupId, int numberOfGuests, boolean empty) {
        this.id = id;
        this.tableGroupId = tableGroupId;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public static OrderTableWithTableGroupResponse of(OrderTable table) {
        return new OrderTableWithTableGroupResponse(table.getId(), table.getTableGroupId(), table.getNumberOfGuests(),
                table.isEmpty());
    }

    public static List<OrderTableWithTableGroupResponse> listOf(List<OrderTable> tables) {
        return tables.stream()
                .map(OrderTableWithTableGroupResponse::of)
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
