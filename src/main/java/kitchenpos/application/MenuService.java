package kitchenpos.application;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.repository.MenuGroupRepository;
import kitchenpos.repository.MenuProductRepository;
import kitchenpos.repository.MenuRepository;
import kitchenpos.repository.ProductRepository;
import kitchenpos.ui.dto.MenuCreateRequest;
import kitchenpos.ui.dto.MenuProductRequest;
import kitchenpos.ui.dto.MenuProductResponse;
import kitchenpos.ui.dto.MenuResponse;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuProductRepository menuProductRepository;
    private final ProductRepository productRepository;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuGroupRepository menuGroupRepository,
            final MenuProductRepository menuProductRepository,
            final ProductRepository productRepository
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuProductRepository = menuProductRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public MenuResponse create(final MenuCreateRequest createRequest) {

        MenuGroup menuGroup = menuGroupRepository.findById(createRequest.getMenuGroupId())
                .orElseThrow(IllegalArgumentException::new);

        Menu menu = createRequest.toEntity(menuGroup);

        final BigDecimal price = createRequest.getPrice();
        List<MenuProductRequest> menuProducts = createRequest.getMenuProducts();

        List<Long> productIds = menuProducts.stream()
                .map(MenuProductRequest::getProductId)
                .collect(toList());

        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException();
        }

        BigDecimal sum = menuProducts.stream()
                .map(menuProductRequest -> findProduct(products, menuProductRequest).getPrice()
                        .multiply(BigDecimal.valueOf(menuProductRequest.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(price) < 0) {
            throw new IllegalArgumentException();
        }

        final Menu savedMenu = menuRepository.save(menu);

        List<MenuProduct> menuProductList = menuProducts.stream()
                .map(menuProductRequest -> {
                    Product product = findProduct(products, menuProductRequest);
                    Long quantity = menuProductRequest.getQuantity();
                    return new MenuProduct(savedMenu, product, quantity);
                })
                .collect(toList());

        List<MenuProduct> menuProducts1 = menuProductRepository.saveAll(menuProductList);
        List<MenuProductResponse> menuProductResponses = MenuProductResponse.listOf(menuProducts1);
        return MenuResponse.of(savedMenu, menuProductResponses);
    }

    private Product findProduct(List<Product> products, MenuProductRequest menuProduct) {
        return products.stream()
                .filter(product -> Objects.equals(product.getId(), menuProduct.getProductId()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<MenuResponse> list() {
        final List<Menu> menus = menuRepository.findAll();
        List<MenuProduct> menuProducts = menuProductRepository.findByMenuIn(menus);
        List<MenuResponse> result = new ArrayList<>();
        for (Menu menu : menus) {
            List<MenuProductResponse> collect = menuProducts.stream()
                    .filter(menuProduct -> Objects.equals(menuProduct.getMenuId(), menu.getId()))
                    .collect(collectingAndThen(toList(), MenuProductResponse::listOf));
            result.add(MenuResponse.of(menu, collect));
        }

        return result;
    }
}
