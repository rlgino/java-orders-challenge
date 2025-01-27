package com.rlgino.OrdersService.catalog.application;

import com.rlgino.OrdersService.catalog.domain.exceptions.DuplicatedProductException;
import com.rlgino.OrdersService.catalog.domain.Product;
import com.rlgino.OrdersService.catalog.domain.ProductRepository;
import com.rlgino.OrdersService.catalog.domain.exceptions.ProductNotExistsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> findProductByID(UUID id) {
        return this.productRepository.findById(id);
    }

    public List<Product> listAllProducts() {
        final Iterable<Product> products = this.productRepository.findAll();
        final List<Product> result = new ArrayList<>();
        products.forEach(p -> {
            if (p.getDeleteAt() == null) result.add(p);
        });
        return result;
    }

    public void createProduct(Product product) {
        final Optional<Product> existentProduct = this.productRepository.findById(product.getId());
        if (existentProduct.isPresent()) throw new DuplicatedProductException(product.getId());

        this.productRepository.save(product);
    }

    public void updateProduct(Product product) {
        final Optional<Product> existentProduct = this.productRepository.findById(product.getId());
        if (existentProduct.isEmpty()) throw new ProductNotExistsException(product.getId());

        this.productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        final Optional<Product> product = this.productRepository.findById(id);
        if (product.isEmpty()) throw new ProductNotExistsException(id);
        final Product prod = product.get();
        final Product deletedProduct = prod.delete();
        this.productRepository.save(deletedProduct);
    }
}
