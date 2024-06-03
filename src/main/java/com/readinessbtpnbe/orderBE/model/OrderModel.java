package com.readinessbtpnbe.orderBE.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Table(name = "orders")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel implements java.io.Serializable {

   private static final long serialVersionUID = -5894679636266655135L;

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "order_id", updatable = false, nullable = false)
   private int orderId;

   @Column(name = "order_code")
   private String orderCode;

   @Column(name = "order_date", nullable = false, updatable = false)
   private LocalDateTime orderDate;

   @Column(name = "total_price")
   private int totalPrice;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "customer_id", nullable = false)
   private CustomerModel customer;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "items_id", nullable = false)
   private ItemModel item;

   @Column(name = "quantity")
   private int quantity;

}
