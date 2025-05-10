package com.example.eCommerce.dto;


import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CardItemDTO {

    private Long id;
    private Long productId;

    @Positive
    private  Integer quantity;
}
