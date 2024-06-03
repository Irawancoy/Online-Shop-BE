package com.readinessbtpnbe.orderBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readinessbtpnbe.orderBE.model.OrderModel;

public interface OrderRepository extends JpaRepository<OrderModel, Integer> {
   

}
