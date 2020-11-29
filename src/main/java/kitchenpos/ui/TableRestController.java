package kitchenpos.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kitchenpos.application.TableService;
import kitchenpos.ui.dto.OrderTableChangeEmptyRequest;
import kitchenpos.ui.dto.OrderTableChangeGuestRequest;
import kitchenpos.ui.dto.OrderTableCreateRequest;
import kitchenpos.ui.dto.OrderTableResponse;
import kitchenpos.ui.dto.OrderTableWithTableGroupResponse;

@RestController
public class TableRestController {
    
    private final TableService tableService;

    public TableRestController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping("/api/tables")
    public ResponseEntity<OrderTableResponse> create(@RequestBody final OrderTableCreateRequest createRequest) {
        final OrderTableResponse created = tableService.create(createRequest);
        final URI uri = URI.create("/api/tables/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    @GetMapping("/api/tables")
    public ResponseEntity<List<OrderTableWithTableGroupResponse>> list() {
        return ResponseEntity.ok()
                .body(tableService.list())
                ;
    }

    @PutMapping("/api/tables/{orderTableId}/empty")
    public ResponseEntity<OrderTableResponse> changeEmpty(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTableChangeEmptyRequest changeEmptyRequest
    ) {
        return ResponseEntity.ok()
                .body(tableService.changeEmpty(orderTableId, changeEmptyRequest))
                ;
    }

    @PutMapping("/api/tables/{orderTableId}/number-of-guests")
    public ResponseEntity<OrderTableWithTableGroupResponse> changeNumberOfGuests(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTableChangeGuestRequest changeGuestRequest
    ) {
        return ResponseEntity.ok()
                .body(tableService.changeNumberOfGuests(orderTableId, changeGuestRequest))
                ;
    }
}
