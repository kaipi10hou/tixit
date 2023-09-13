package kr.tixit.homework.products;

import kr.tixit.homework.domain.product.Product;
import kr.tixit.homework.domain.product.ProductRepository;
import kr.tixit.homework.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductsService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }
    public Optional<Product> findByProductNo(String productNo) {
        return productRepository.findByProductNo(productNo);
    }

    @Transactional
    public String save(Product product) {
        return productRepository.save(product).getProductNo();
    }

    public synchronized void purchase(String productNo, Long quantity) throws SoldOutException {
        Optional<Product> product = productRepository.findByProductNo(productNo);
        if (quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
        }

        if (product.isPresent()) {
            if (quantity <= product.get().getInventory()) {
                product.get().setInventory(product.get().getInventory() - quantity);
                productRepository.saveAndFlush(product.get());
            } else {
                throw new SoldOutException();
            }
        }
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }
}
