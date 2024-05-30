package com.readinessbtpnbe.orderBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.readinessbtpnbe.orderBE.model.ItemModel;

public interface ItemRepository extends JpaRepository<ItemModel,Integer>{

}
