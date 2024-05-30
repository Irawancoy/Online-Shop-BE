package com.readinessbtpnbe.orderBE.dto.response;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;

@Data
public class DaftarCustomersDTO {
      private int customerId;
      private String customerName;
      private String customerAddress;
      private String customerCode;
      private String customerPhone;
      private int isActive;
      private DateTime lastOrderDate;
      private String pic;

}
