package com.cavasini.CartMicroservice.controller;

import com.cavasini.CartMicroservice.model.Product;
import com.cavasini.CartMicroservice.service.CartService;
import com.cavasini.CartMicroservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    CartService cartService;


    @GetMapping
    public List<Product> getProducts(){
        return productService.getProducts();
    }


    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") UUID id){
        Product product = productService.getProductById(id);
        return product;
    }






}
