package com.readinessbtpnbe.orderBE.dto.request;

import lombok.Data;

@Data
public class CreateOrderRequest {

   private int customerId;
   private int itemsId;
   private int quantity;

}
