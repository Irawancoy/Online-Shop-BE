package com.readinessbtpnbe.orderBE.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;

@Table(name = "items")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemModel implements java.io.Serializable {

   private static final long serialVersionUID = -5894679636266655135L;

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "items_id", updatable = false, nullable = false)
   private int itemId;

   @Column(name = "items_name")
   private String itemName;

   @Column(name = "items_code")
   private String itemsCode;

   @Column(name = "stock")
   private int stock;

   @Column(name = "price")
   private int price;

   @Column(name = "is_available")
   private int isAvailable;

   @Column(name = "last_re_stock")
   private DateTime lastReStock;

}
