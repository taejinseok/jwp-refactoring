package kitchenpos.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.domain.Product;
import kitchenpos.repository.ProductRepository;
import kitchenpos.ui.dto.ProductCreateRequest;
import kitchenpos.ui.dto.ProductResponse;

@Service
public class ProductService {

    private final ProductRepository productDao;

    public ProductService(final ProductRepository productDao) {
        this.productDao = productDao;
    }

    @Transactional
    public ProductResponse create(final ProductCreateRequest createRequest) {
        Product newProduct = productDao.save(createRequest.toEntity());
        return ProductResponse.of(newProduct);
    }

    public List<ProductResponse> list() {
        return ProductResponse.listOf(productDao.findAll());
    }
}
