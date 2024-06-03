package com.readinessbtpnbe.orderBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readinessbtpnbe.orderBE.model.CustomerModel;

public interface CustomerRepository extends JpaRepository<CustomerModel, Integer> {
   
}
