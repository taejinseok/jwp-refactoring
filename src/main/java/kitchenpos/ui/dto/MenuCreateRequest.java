package kitchenpos.ui.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

public class MenuCreateRequest {

    @NotBlank
    private String name;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @NotNull
    private Long menuGroupId;

    private List<MenuProductRequest> menuProducts;

    private MenuCreateRequest() {
    }

    public MenuCreateRequest(String name, BigDecimal price, Long menuGroupId,
            List<MenuProductRequest> menuProducts) {
        this.name = name;
        this.price = price;
        this.menuGroupId = menuGroupId;
        this.menuProducts = menuProducts;
    }

    public Menu toEntity(MenuGroup menuGroup) {
        return new Menu(name, price, menuGroup);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getMenuGroupId() {
        return menuGroupId;
    }

    public List<MenuProductRequest> getMenuProducts() {
        return menuProducts;
    }
}
