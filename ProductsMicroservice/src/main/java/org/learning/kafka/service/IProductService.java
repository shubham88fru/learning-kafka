package org.learning.kafka.service;

import org.learning.kafka.rest.CreateProductRestModel;

import java.util.concurrent.ExecutionException;

public interface IProductService {
    String createProduct(CreateProductRestModel product) throws ExecutionException, InterruptedException;
}
