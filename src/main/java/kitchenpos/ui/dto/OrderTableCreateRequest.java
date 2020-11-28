package kitchenpos.ui.dto;

import kitchenpos.domain.OrderTable2;

public class OrderTableCreateRequest {

    private int numberOfGuests;
    private boolean empty;

    private OrderTableCreateRequest() {
    }

    public OrderTableCreateRequest(int numberOfGuests, boolean empty) {
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public OrderTable2 toEntity() {
        return new OrderTable2(numberOfGuests, empty);
    }
}
