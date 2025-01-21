package com.cavasini.CartMicroservice.repository;

import com.cavasini.CartMicroservice.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID> {

    List<Product> findAllByOrderByCreatedAtDesc();
    List<Product> findAll();

    @Query("SELECT p FROM Product p ORDER BY p.createdAt ASC")
    Product findFirstProduct();
}
