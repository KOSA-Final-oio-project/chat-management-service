package com.oio.chatservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDto {

    private String productName; // 상품명
    private String productPrice; // 상품가격
    private String productStatus; // 대여상태
    private String productImage; // 썸네일만

} // end class
