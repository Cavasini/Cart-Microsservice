package com.cavasini.CartMicroservice.service;

import com.cavasini.CartMicroservice.model.Product;
import com.cavasini.CartMicroservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String PRODUCT_KEY = "product";

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Cacheable("Products")
    public Product getProductById(UUID id){
       return productRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
    }


}
