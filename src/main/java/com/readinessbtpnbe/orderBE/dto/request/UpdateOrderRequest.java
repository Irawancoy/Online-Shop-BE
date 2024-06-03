package com.readinessbtpnbe.orderBE.dto.request;

import lombok.Data;

@Data
public class UpdateOrderRequest {

      private int orderId;
      private int customerId;
      private int itemId;
      private int quantity;

}
