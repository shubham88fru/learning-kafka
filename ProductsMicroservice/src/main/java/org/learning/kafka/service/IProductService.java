package org.learning.kafka.service;

import org.learning.kafka.rest.CreateProductRestModel;

public interface IProductService {
    String createProduct(CreateProductRestModel product);
}
