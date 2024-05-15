package org.learning.kafka.rest;


import org.learning.kafka.service.IProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    IProductService service;

    public ProductController(IProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody CreateProductRestModel product) {
        String productId = service.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
