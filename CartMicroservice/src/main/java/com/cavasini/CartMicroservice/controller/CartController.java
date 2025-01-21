package com.cavasini.CartMicroservice.controller;

import com.cavasini.CartMicroservice.dto.request.AddToCartRequest;
import com.cavasini.CartMicroservice.dto.request.CartUpdateRequest;
import com.cavasini.CartMicroservice.dto.request.DeleteCartProductRequest;
import com.cavasini.CartMicroservice.dto.response.CartResponse;

import com.cavasini.CartMicroservice.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{id}")

    public ResponseEntity<CartResponse> getCart(@PathVariable("id") UUID id) {
        CartResponse response = cartService.getCartResponse(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> addCartProduct(@RequestBody AddToCartRequest request) {
        try {
            cartService.addOrUpdateItem(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Produto adicionado ao carrinho com sucesso!"
            ));
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao adicionar produto ao carrinho", ex);
        }
    }

    @Operation(description = "Change quantity of product in cart\n" +
            "\n" +
            "To use, you must enter the following in the \"action\":\n" +
            "\n" +
            "- to increase quantity: \"increment\"\n" +
            "- to decrease quantity: \"decrement\"")
    @PatchMapping
    public ResponseEntity<?> updateCartProduct(@RequestBody CartUpdateRequest request) {
        try {
            cartService.updateItemQuantity(request);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Quantidade do produto atualizada com sucesso!"
            ));
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao atualizar a quantidade do produto", ex);
        }
    }


    @DeleteMapping
    public ResponseEntity<?> deleteCartProduct(@RequestBody DeleteCartProductRequest request) {
        try {
            cartService.removeItem(request);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Produto removido do carrinho com sucesso!"
            ));
        } catch (IllegalArgumentException ex) {
            // Lança exceção específica capturada pelo GlobalExceptionHandler
            throw ex;
        } catch (Exception ex) {
            // Lança exceção genérica
            throw new RuntimeException("Erro ao remover o produto do carrinho", ex);
        }
    }

}
