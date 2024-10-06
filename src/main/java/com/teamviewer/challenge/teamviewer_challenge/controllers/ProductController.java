package com.teamviewer.challenge.teamviewer_challenge.controllers;

import com.teamviewer.challenge.teamviewer_challenge.application.ProductService;
import com.teamviewer.challenge.teamviewer_challenge.domain.Product;
import com.teamviewer.challenge.teamviewer_challenge.domain.exceptions.DuplicatedProductException;
import com.teamviewer.challenge.teamviewer_challenge.domain.exceptions.ProductNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    public ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity findProduct(@PathVariable String id) {
        UUID parsedID;
        try {
            parsedID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID: " + id, HttpStatus.BAD_REQUEST);
        }
        Optional<Product> productOpt = this.productService.findProductByID(parsedID);
        if (productOpt.isEmpty())
            return new ResponseEntity<>(String.format("Product with ID %s not found", id), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(productOpt.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity saveProduct(@RequestBody Product product) {
        try {
            this.productService.createProduct(product);
        } catch (DuplicatedProductException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity updateProduct(@RequestBody Product product) {
        try {
            this.productService.updateProduct(product);
        } catch (ProductNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id) {
        try {
            final UUID parsedID = UUID.fromString(id);
            this.productService.deleteProduct(parsedID);
        }catch (ProductNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID: " + id, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Product>> listProducts() {
        List<Product> allProducts = this.productService.listAllProducts();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }
}

