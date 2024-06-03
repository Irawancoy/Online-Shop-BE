package com.readinessbtpnbe.orderBE.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DaftarCustomersDTO {
      private int customerId;
      private String customerName;
      private String customerAddress;
      private String customerCode;
      private String customerPhone;
      private int isActive;
      private LocalDateTime lastOrderDate;
      private String pic;

}
