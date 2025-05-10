package com.example.eCommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {

    private Long id;
    private Long userId;
    private List<CardItemDTO> items;

}
