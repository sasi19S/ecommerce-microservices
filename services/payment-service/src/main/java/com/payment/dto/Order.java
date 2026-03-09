package com.payment.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private String productName;
    private int quantity;
    private String status;
}