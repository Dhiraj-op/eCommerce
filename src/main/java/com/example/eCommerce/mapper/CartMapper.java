package com.example.eCommerce.mapper;


import com.example.eCommerce.dto.CardItemDTO;
import com.example.eCommerce.dto.CartDTO;
import com.example.eCommerce.model.Cart;
import com.example.eCommerce.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId",source ="user.id" )
    CartDTO toDTO(Cart cart);

    @Mapping(target = "user.id",source = "userId")
    Cart toEntity(CartDTO cartDTO);

    @Mapping(target = "productId",source = "product.id")
    CardItemDTO toDTO(CartItem cartItem);

    @Mapping(target = "product.id",source = "productId")
    CartItem toEntity(CardItemDTO cardItemDTO);
}

