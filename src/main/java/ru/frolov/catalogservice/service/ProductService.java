package ru.frolov.catalogservice.service;

import ru.frolov.catalogservice.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAllProducts();

    Product createProduct(String title, String details);

    Optional<Product> findProduct(int productId);


    void updateProduct(Integer id, String title, String details);

    void deleteProduct(Integer id);
}
