package com.example.eCommerce.dto;

import lombok.Data;

@Data
public class EmailConformationRequest {
    private String email;
    private String confirmationCode;
}
