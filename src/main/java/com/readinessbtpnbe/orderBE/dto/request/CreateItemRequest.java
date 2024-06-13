package com.readinessbtpnbe.orderBE.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;

@Data
public class CreateItemRequest {
   
      @NotEmpty(message = "Nama Item tidak boleh kosong")
      private String itemName;
      private int stock;
      private int price;
      private int isAvailable;
      private LocalDateTime lastReStock;

}
