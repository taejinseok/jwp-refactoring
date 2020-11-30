package kitchenpos.ui.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import kitchenpos.domain.OrderTable;

public class OrderTableResponse {

    private Long id;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableResponse(Long id, int numberOfGuests, boolean empty) {
        this.id = id;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public static OrderTableResponse of(OrderTable table) {
        return new OrderTableResponse(table.getId(), table.getNumberOfGuests(), table.isEmpty());
    }

    public static List<OrderTableResponse> listOf(List<OrderTable> tables) {
        return tables.stream()
                .map(OrderTableResponse::of)
                .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }
}
