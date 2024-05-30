package com.readinessbtpnbe.orderBE.dto.request;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;

@Data
public class UpdateItemRequest {
   private int itemId;
   private String itemName;
   private String itemCode;
   private int stock;
   private int price;
   private int isAvailable;
   private DateTime lastReStock;



}
