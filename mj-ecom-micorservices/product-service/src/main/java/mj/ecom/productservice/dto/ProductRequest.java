package mj.ecom.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
}
