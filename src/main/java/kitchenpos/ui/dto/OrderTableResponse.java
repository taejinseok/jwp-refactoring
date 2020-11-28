package kitchenpos.ui.dto;

import kitchenpos.domain.OrderTable2;

public class OrderTableResponse {

    private Long id;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableResponse(Long id, int numberOfGuests, boolean empty) {
        this.id = id;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public static OrderTableResponse of(OrderTable2 table) {
        return new OrderTableResponse(table.getId(), table.getNumberOfGuests(), table.isEmpty());
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
