package com.example.eCommerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    private Integer id;
    @NotBlank(message = "content is required")
    private String content;
    @Min(1)
    @Max(5)
    private Integer score;
    private Long userId;
}
