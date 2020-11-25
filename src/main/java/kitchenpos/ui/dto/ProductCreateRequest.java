package kitchenpos.ui.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import kitchenpos.domain.Product;

public class ProductCreateRequest {

    @NotBlank
    private String name;

    @Min(0)
    private BigDecimal price;

    private ProductCreateRequest() {
    }

    public ProductCreateRequest(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Product toEntity() {
        return new Product(name, price);
    }
}
