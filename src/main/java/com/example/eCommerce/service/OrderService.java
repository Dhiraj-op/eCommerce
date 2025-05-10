package com.example.eCommerce.service;


import com.example.eCommerce.dto.CartDTO;
import com.example.eCommerce.dto.OrderDTO;
import com.example.eCommerce.exception.InsufficientStockException;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.mapper.CartMapper;
import com.example.eCommerce.mapper.OrderMapper;
import com.example.eCommerce.model.*;
import com.example.eCommerce.repoitory.OrderRepository;
import com.example.eCommerce.repoitory.ProductRepository;
import com.example.eCommerce.repoitory.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;

    @Transactional
    public OrderDTO createOrder(Long userId , String address, String phoneNumber){

        User user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        if(!user.isEmailConfirmation()){
            throw new IllegalStateException("Email not confirmed. Please confirm email before placing order");
        }

        CartDTO cartDTO = cartService.getCart(userId);
        Cart cart = cartMapper.toEntity(cartDTO);

        if(cart.getItems().isEmpty()){
            throw new IllegalStateException("Can not create an order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = createOrderItems(order,cart);

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        try {
            emailService.sendOrderConformation(savedOrder);
        }catch (MailException e){
            logger.error("Failed to send order confirmation email for orderID");
        }
        System.out.println("hello10");
        return orderMapper.toDTO(savedOrder);
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems().stream().map(cartItem -> {

            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(()->new EntityNotFoundException("Product not found with id"+ cartItem.getProduct().getId()));

            if(product.getQuantity()==null){
                throw new IllegalStateException("Product quantity is not ser for the product "+ product.getName());
            }
            if(product.getQuantity()<cartItem.getQuantity()){
                throw new InsufficientStockException("Not enough stock for the product"+ product.getName());
            }

            product.setQuantity(product.getQuantity()- cartItem.getQuantity());

            productRepository.save(product);

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            return orderItem;

        }).collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrder(){
        return orderMapper.toDTOs(orderRepository.findAll());
    }

    public List<OrderDTO> getUserOrders(Long userId){
        return orderMapper.toDTOs(orderRepository.findByUserId(userId));
    }

    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus orderStatus){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));

        order.setStatus(orderStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }


}
