package com.cavasini.CartMicroservice.service;

import com.cavasini.CartMicroservice.dto.event.CartUpdateEvent;
import com.cavasini.CartMicroservice.dto.request.AddToCartRequest;
import com.cavasini.CartMicroservice.dto.request.CartUpdateRequest;
import com.cavasini.CartMicroservice.dto.request.DeleteCartProductRequest;
import com.cavasini.CartMicroservice.dto.response.CartItemDTO;
import com.cavasini.CartMicroservice.dto.response.CartResponse;
import com.cavasini.CartMicroservice.mapper.CartMapper;
import com.cavasini.CartMicroservice.messaging.producer.CartEventPublisher;
import com.cavasini.CartMicroservice.model.Cart;
import com.cavasini.CartMicroservice.model.CartItem;
import com.cavasini.CartMicroservice.model.Product;
import com.cavasini.CartMicroservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CartService {

    @Autowired private RedisTemplate<String, Object> redisTemplate;
    @Autowired private CartEventPublisher cartEventPublisher;
    @Autowired private ProductRepository productRepository;
    @Autowired private DatabaseService databaseService;

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private static final String CART_PREFIX = "cart:";
    private final CartMapper mapper = new CartMapper();

    public Cart getCart(UUID userId) {
        System.out.println(1);
        String key = CART_PREFIX + userId.toString();

        Cart cart = (Cart) redisTemplate.opsForValue().get(key);
        System.out.println(2);
        if (cart == null) {
            cart = fetchCartFromDatabase(userId);
            System.out.println(3);
            if (cart != null) {
                saveCartRedisCache(userId, cart);
                log.info("Carrinho carregado do banco de dados e armazenado no Redis para o usuário: {}", userId);
            } else {
                log.info("Carrinho não encontrado no banco de dados para o usuário: {}", userId);
            }
        } else {
            log.info("Carrinho encontrado no Redis para o usuário: {}", userId);
        }

        return cart;
    }

    public CartResponse getCartResponse(UUID id) {

        Cart cart = getCart(id);

        if (cart == null) {
            return mapper.toEmptyCartResponse(id);
        }

        BigDecimal total = cart.getItems().stream()
                .map(cartItem -> cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        List<CartItemDTO> items = cart.getItems().stream()
                .map(cartItem -> new CartItemDTO(
                        cartItem.getId(),
                        cartItem.getProductId(),
                        cartItem.getName(),
                        cartItem.getQuantity(),
                        cartItem.getPrice()
                ))
                .toList();

        return new CartResponse(cart.getUserId(), items, total);

    }

    public void addOrUpdateItem(AddToCartRequest request) {
        validateRequest(request);
        validateQuantity(request.quantity());

        Cart cart = Optional.ofNullable(getCart(request.userId()))
                .orElseGet(() -> new Cart(request.userId()));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartUpdateRequest cartUpdateRequest = new CartUpdateRequest(
                    request.userId(),
                    request.productId(),
                    "increment",
                    request.quantity()
            );
            incrementQuantity(cartUpdateRequest, cart);
        } else {
            addItem(request, cart);
        }

        saveCartRedisCache(request.userId(), cart);
    }


    private void addItem(AddToCartRequest request, Cart cart) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para o ID: " + request.productId()));

        CartItem cartItem = mapper.toCartItem(request, product);
        cart.getItems().add(cartItem);

        CartUpdateEvent event = mapper.toAddToCartEvent(cartItem.getId(), request, product);
        cartEventPublisher.sendMessage(event, "cart.addItem");
        log.info("Produto adicionado ao carrinho: {}", cartItem);
    }


    public void updateItemQuantity(CartUpdateRequest request) {
        validateRequest(request);
        validateQuantity(request.quantity());

        switch (request.action().toLowerCase()) {
            case "increment" -> incrementQuantity(request, null);
            case "decrement" -> decrementQuantity(request);
            default -> throw new IllegalArgumentException("Ação inválida: " + request.action());
        }
    }


    private void incrementQuantity(CartUpdateRequest request, Cart cart) {
        if (cart == null) {
            cart = getCart(request.userId());
        }

        Optional<CartItem> item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(request.productId()))
                .findFirst();

        if (item.isPresent()) {
            CartItem cartItem = item.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
            log.info("Quantidade incrementada para o produto {}: {}", cartItem.getProductId(), cartItem.getQuantity());
            CartUpdateEvent event = mapper.toUpdateCartEvent(cartItem.getId(), request.userId(), cartItem.getQuantity());
            cartEventPublisher.sendMessage(event, "cart.updateItemQuantity");
        } else {
            throw new NoSuchElementException("Produto não encontrado no carrinho.");
        }

        saveCartRedisCache(request.userId(), cart);
    }


    private void decrementQuantity(CartUpdateRequest request) {
        Cart cart = getCart(request.userId());

        Optional<CartItem> item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(request.productId()))
                .findFirst();

        if (item.isPresent()) {
            CartItem cartItem = item.get();
            int newQuantity = cartItem.getQuantity() - request.quantity();

            if (newQuantity < 1) {
                throw new IllegalArgumentException("Quantidade mínima permitida é 1.");
            }

            cartItem.setQuantity(newQuantity);
            log.info("Quantidade decrementada para o produto {}: {}", cartItem.getProductId(), cartItem.getQuantity());

//            CartUpdateEvent event = createUpdateCartEvent(cartItem.getId(), request.userId(), cartItem.getQuantity());
            CartUpdateEvent event = mapper.toUpdateCartEvent(cartItem.getId(), request.userId(), cartItem.getQuantity());
            cartEventPublisher.sendMessage(event, "cart.updateItemQuantity");
        } else {
            throw new NoSuchElementException("Produto não encontrado no carrinho.");
        }

        saveCartRedisCache(request.userId(), cart);
    }

    public void removeItem(DeleteCartProductRequest request) {
        Cart cart = getCart(request.userId());

        if (cart == null || cart.getItems().isEmpty()) {
            throw new NoSuchElementException("Carrinho não encontrado ou vazio para o usuário: " + request.userId());
        }

        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();

        if (itemToRemove.isEmpty()) {
            throw new IllegalStateException("O produto com ID " + request.productId() + " não foi encontrado no carrinho.");
        }

        CartItem removedItem = itemToRemove.get();
        cart.getItems().remove(removedItem);
        log.info("Produto removido do carrinho: {}", removedItem);

        if (cart.getItems().isEmpty()) {
            log.info("Carrinho do usuário {} está vazio após a remoção.", request.userId());
        }

//        CartUpdateEvent event = createDeleteCartEvent(removedItem.getId(), request.userId());
        CartUpdateEvent event = mapper.toDeleteCartEvent(removedItem.getId(), request.userId());
        cartEventPublisher.sendMessage(event, "cart.removeItem");

        saveCartRedisCache(request.userId(), cart);
    }

    public void saveCartRedisCache(UUID userId, Cart cart) {
        String key = CART_PREFIX + userId;
        Duration cacheExpirationDuration = Duration.ofDays(20);
        redisTemplate.opsForValue().set(key, cart, cacheExpirationDuration);
    }

    private void validateRequest(Object request) {
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantidade mínima permitida é 1.");
        }
    }

    public Cart fetchCartFromDatabase(UUID userId) {
        List<CartItem> items = databaseService.getCart(userId);
        System.out.println(5);
        if (items.isEmpty()) {
            System.out.println(6);
            return null;
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(items);
        System.out.println(7);
        return cart;
    }

}