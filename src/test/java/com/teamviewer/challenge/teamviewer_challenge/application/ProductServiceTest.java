package com.teamviewer.challenge.teamviewer_challenge.application;

import com.teamviewer.challenge.teamviewer_challenge.domain.exceptions.DuplicatedProductException;
import com.teamviewer.challenge.teamviewer_challenge.domain.Product;
import com.teamviewer.challenge.teamviewer_challenge.domain.ProductMother;
import com.teamviewer.challenge.teamviewer_challenge.domain.ProductRepository;
import com.teamviewer.challenge.teamviewer_challenge.domain.exceptions.ProductNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public ProductService productService;

    @BeforeEach
    public void setup() {
        this.productRepository = Mockito.mock(ProductRepository.class);
        this.productService = new ProductService(this.productRepository);
    }

    @Test
    public void findProduct_returnEmpty() {
        final UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Product> product = this.productService.findProductByID(id);
        assertTrue(product.isEmpty());
    }

    @Test
    public void findProduct_returnValidProduct() {
        final Product result = ProductMother.dummy();
        when(productRepository.findById(result.getId())).thenReturn(Optional.of(result));
        Optional<Product> product = this.productService.findProductByID(result.getId());
        assertFalse(product.isEmpty());
        assertEquals(product.get(), result);
    }

    @Test
    public void findProduct_returnAnException() {
        final Product result = ProductMother.dummy();
        when(productRepository.findById(result.getId())).thenThrow(new RuntimeException("Custom exception"));

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> this.productService.findProductByID(result.getId()));
        assertTrue(exception.getMessage().contains("Custom exception"));
    }

    @Test
    public void listProducts_returnListWithTwoProducts() {
        final Product product1 = new Product();
        final Product product2 = new Product();
        Product deletedProduct = new Product();
        deletedProduct = deletedProduct.delete();
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, deletedProduct));

        final List<Product> products = productService.listAllProducts();

        assertEquals(2, products.size());
    }

    @Test
    public void listProducts_returnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        final List<Product> products = productService.listAllProducts();

        assertTrue(products.isEmpty());
    }

    @Test
    public void createProduct_shouldSaveProduct() {
        final Product product = ProductMother.dummy();
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.empty());

        productService.createProduct(product);

        verify(this.productRepository, times(1)).save(product);
    }

    @Test
    public void createProduct_productIdAlreadyExists() {
        final Product product = ProductMother.dummy();
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        DuplicatedProductException exception = assertThrows(DuplicatedProductException.class, () -> productService.createProduct(product));

        assertTrue(exception.getMessage().contains("Duplicated product for ID"));
    }

    @Test
    public void updateProduct_shouldUpdateProduct() {
        final Product product = ProductMother.dummy();
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        final Product updatedProduct = new Product(product.getId(), "new name", BigDecimal.TWO);

        productService.updateProduct(updatedProduct);

        verify(this.productRepository, times(1)).save(updatedProduct);
    }

    @Test
    public void updateProduct_productIdNoExists() {
        final Product product = ProductMother.dummy();
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.empty());

        ProductNotExistsException exception = assertThrows(ProductNotExistsException.class, () -> productService.updateProduct(product));

        assertTrue(exception.getMessage().contains("Product not found for ID"));
    }

    @Test
    public void deleteProduct_shouldDeleteProduct() {
        final Product product = ProductMother.dummy();
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        final Product updatedProduct = new Product(product.getId(), "new name", BigDecimal.TWO);

        productService.deleteProduct(updatedProduct.getId());

        verify(this.productRepository, times(1)).save(Mockito.isA(Product.class));
    }

    @Test
    public void deleteProduct_productIdNoExists() {
        final Product product = ProductMother.dummy();
        when(this.productRepository.findById(product.getId())).thenReturn(Optional.empty());

        ProductNotExistsException exception = assertThrows(ProductNotExistsException.class, () -> productService.deleteProduct(product.getId()));

        assertTrue(exception.getMessage().contains("Product not found for ID"));
    }
}
