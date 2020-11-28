package kitchenpos.ui.dto;

import kitchenpos.domain.OrderTable2;

public class OrderTableCreateResponse {

    private Long id;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableCreateResponse(Long id, int numberOfGuests, boolean empty) {
        this.id = id;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public static OrderTableCreateResponse of(OrderTable2 table) {
        return new OrderTableCreateResponse(table.getId(), table.getNumberOfGuests(), table.isEmpty());
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
