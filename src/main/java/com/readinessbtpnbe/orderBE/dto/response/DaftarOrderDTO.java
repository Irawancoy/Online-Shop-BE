package com.readinessbtpnbe.orderBE.dto.response;

import lombok.Data;

@Data
public class DaftarOrderDTO {
   private int orderId;
   private String orderCode;
   private String orderDate;
   private DaftarCustomersDTO customer;
   private DaftarItemDTO item;
   private int quantity;
   private int totalPrice;

}
