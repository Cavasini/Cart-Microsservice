package com.cavasini.CartMicroservice.service;

import com.cavasini.CartMicroservice.model.CartItem;
import com.cavasini.CartMicroservice.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DatabaseService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<CartItem> getCart(UUID userID){
        System.out.println(4);
        return cartItemRepository.findByUserId(userID);
    }

    public void addItemToTheCart(CartItem cartItem){
        cartItemRepository.save(cartItem);
    }

    public void updateQuantity(UUID itemId, Integer quantity){
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(itemId);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        } else {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }

    }

    public void deleteItem(UUID id){
        cartItemRepository.deleteById(id);
    }

}
