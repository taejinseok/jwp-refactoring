package kitchenpos.ui.dto;

import java.util.List;

public class TableGroupCreateRequest {

    private List<Long> orderTableIds;

    private TableGroupCreateRequest() {
    }

    public TableGroupCreateRequest(List<Long> orderTableIds) {
        this.orderTableIds = orderTableIds;
    }

    public List<Long> getOrderTableIds() {
        return orderTableIds;
    }
}
