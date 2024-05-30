package com.readinessbtpnbe.orderBE.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;
import lombok.Builder;
import lombok.Data;

@Table(name = "customers")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModel implements java.io.Serializable {
   
   private static final long serialVersionUID = -5894679636266655135L;

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   @Column(name="customer_id",updatable = false,nullable = false)
   private int customerId;

   @Column(name="customer_name")
   private String customerName;

   @Column(name="customer_address")
   private String customerAddress;

   @Column(name="customer_code")
   private String customerCode;

   @Column(name="customer_phone")
   private String customerPhone;

   @Column(name="is_active")
   private int isActive;

   @Column(name="last_order_date")
   private DateTime lastOrderDate;

   @Column(name="pic")
   private String pic;



}
