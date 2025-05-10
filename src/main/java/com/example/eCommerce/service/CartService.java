package com.example.eCommerce.service;


import com.example.eCommerce.dto.CartDTO;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.mapper.CartMapper;
import com.example.eCommerce.model.Cart;
import com.example.eCommerce.model.CartItem;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.model.User;
import com.example.eCommerce.repoitory.CartRepository;
import com.example.eCommerce.repoitory.ProductRepository;
import com.example.eCommerce.repoitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartDTO addToCart(Long userId,Long productId, Integer quantity){

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

//        if(product.getQuantity()<quantity){
//            throw new InsufficientStockException("Not enough available");
//        }
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null,user,new ArrayList<>()));

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item ->item.getProduct().getId().equals(productId))
                .findFirst();

        if(existingCartItem.isPresent()){
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
        }else{
//            CartItem cartItem = new CartItem(null,cart,product,quantity);
//            cart.getItems().add(cartItem);

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);

        }
        Cart saveCart = cartRepository.save(cart);
        return cartMapper.toDTO(saveCart);
    }

    public CartDTO getCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart not found"));
        System.out.println("CartItem Quantity: " + cart.getItems().get(1).getQuantity());
        return cartMapper.toDTO(cart);

    }

    public void clearCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void removeCartItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        cartRepository.save(cart);
    }
}
