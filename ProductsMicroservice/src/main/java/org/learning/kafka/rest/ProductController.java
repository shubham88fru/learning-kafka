package org.learning.kafka.rest;


import org.learning.kafka.service.IProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/products")
public class ProductController {

    IProductService service;

    public ProductController(IProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody CreateProductRestModel product) {
        String productId = null;
        try {
            productId = service.createProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(new Date(), e.getMessage(), "/product"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
