package com.readinessbtpnbe.orderBE.dto.response;

import lombok.Data;
@Data
public class DaftarCustomersDTO {
      private int customerId;
      private String customerName;
      private String customerAddress;
      private String customerCode;
      private String customerPhone;
      private int isActive;
      private String lastOrderDate;
      private String pic;

}
