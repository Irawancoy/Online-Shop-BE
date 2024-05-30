package com.readinessbtpnbe.orderBE.dto.response;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;

@Data
public class DaftarItemDTO {
      private int itemId;
      private String itemName;
      private String itemCode;
      private int stock;
      private int price;
      private int isAvailable;
      private DateTime lastReStock;

}
