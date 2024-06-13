package com.readinessbtpnbe.orderBE.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.readinessbtpnbe.orderBE.dto.request.CreateOrderRequest;
import com.readinessbtpnbe.orderBE.dto.request.UpdateOrderRequest;
import com.readinessbtpnbe.orderBE.dto.response.DaftarCustomersDTO;
import com.readinessbtpnbe.orderBE.dto.response.DaftarItemDTO;
import com.readinessbtpnbe.orderBE.dto.response.DaftarOrderDTO;
import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import com.readinessbtpnbe.orderBE.model.CustomerModel;
import com.readinessbtpnbe.orderBE.model.ItemModel;
import com.readinessbtpnbe.orderBE.model.OrderModel;
import com.readinessbtpnbe.orderBE.repository.CustomerRepository;
import com.readinessbtpnbe.orderBE.repository.ItemRepository;
import com.readinessbtpnbe.orderBE.repository.OrderRepository;
import com.readinessbtpnbe.orderBE.dto.response.ResponseBodyDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

   @Autowired
   private CustomerRepository customerRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Autowired
   private OrderRepository orderRepository;

   @Transactional
   public MessageResponse create(CreateOrderRequest request) {
      log.info("request: " + request);
      try {
         Optional<CustomerModel> cusOptional = customerRepository.findById(request.getCustomerId());

         if (!cusOptional.isPresent()) {
            return new MessageResponse("Customer Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         }
         Optional<ItemModel> itemOptional = itemRepository.findById(request.getItemId());
         log.info("itemOptional: " + itemOptional);

         if (!itemOptional.isPresent()) {
            return new MessageResponse("Item Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         }
         int totalPrice = request.getQuantity() * itemRepository.findById(request.getItemId()).get().getPrice();

         ItemModel itemModel = itemRepository.findById(request.getItemId()).get();

         if (itemModel.getStock() == 0) {
            itemModel.setIsAvailable(0);
            itemRepository.save(itemModel);
         }
         if (request.getQuantity() > itemModel.getStock()) {
            return new MessageResponse("Stock Item Tidak Cukup, Stock Tersedia " + itemModel.getStock(),
                  HttpStatus.BAD_REQUEST.value(),
                  "ERROR");
         } else {
            itemModel.setStock(itemModel.getStock() - request.getQuantity());
            itemRepository.save(itemModel);
         }


         OrderModel orderModel = OrderModel.builder()
         .customer(customerRepository.findById(request.getCustomerId()).get())
               .item(itemRepository.findById(request.getItemId()).get()).quantity(request.getQuantity())
               .orderCode(generateOrderCode(request.getCustomerId(), request.getItemId()))
               .totalPrice(totalPrice)
               .orderDate(
                     new java.sql.Date(System.currentTimeMillis()))
         .build();
         orderRepository.save(orderModel);

         
         CustomerModel customerModel = customerRepository.findById(request.getCustomerId()).get();
         customerModel.setLastOrderDate(orderModel.getOrderDate());
         customerRepository.save(customerModel);

         return new MessageResponse("Order " + orderModel.getOrderCode() + " Berhasil Ditambahkan",
               HttpStatus.OK.value(),
               "OK");
      } catch (Exception e) {
         return new MessageResponse("Order Gagal Ditambahkan", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   @Transactional
   public MessageResponse update(UpdateOrderRequest request) {
      try {
         // update
         Optional<OrderModel> orderModelOptional = orderRepository.findById(request.getOrderId());
         Optional<ItemModel> itemOptional = itemRepository.findById(request.getItemId());
         Optional<CustomerModel> cusOptional = customerRepository.findById(request.getCustomerId());
         log.info("orderModelOptional: " + orderModelOptional);
         log.info("itemOptional: " + itemOptional);
         log.info("cusOptional: " + cusOptional);

         if (!orderModelOptional.isPresent()) {
            return new MessageResponse("Order Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         } else {
            OrderModel orderModel = orderModelOptional.get();
            // jika request null atau empty maka jangan dioperasikan

            if (!cusOptional.isPresent()) {
               return new MessageResponse("Customer Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
            } else {
               CustomerModel customerModel = cusOptional.get();
               customerModel.setLastOrderDate(orderModel.getOrderDate());
               customerRepository.save(customerModel);
            }

            if (!itemOptional.isPresent()) {
               return new MessageResponse("Item Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
            } else {
               ItemModel itemModel = itemOptional.get();
               if (itemModel.getStock() == 0) {
                  itemModel.setIsAvailable(0);
                  itemRepository.save(itemModel);
               }
               if (request.getQuantity() > itemModel.getStock()) {
                  return new MessageResponse("Stock Item Tidak Cukup, Stock Tersedia " + itemModel.getStock(),
                        HttpStatus.BAD_REQUEST.value(),
                        "ERROR");
               } else {
                  // itemModel.setStock(itemModel.getStock()+ request.getQuantity());
                  int oldQuantity = orderModel.getQuantity();
                  itemModel.setStock(itemModel.getStock() + oldQuantity);
                  itemModel.setStock(itemModel.getStock() - request.getQuantity());
                  itemRepository.save(itemModel);
               }
               if (request.getQuantity() != 0) {
                  int totalPrice = request.getQuantity()
                        * itemRepository.findById(request.getItemId()).get().getPrice();
                  orderModel.setTotalPrice(totalPrice);
                  orderModel.setQuantity(request.getQuantity());
               }
            }

            orderModel.setOrderCode(generateOrderCode(request.getCustomerId(),
                  request.getItemId()));

            orderRepository.save(orderModel);
            return new MessageResponse("Order Berhasil Diupdate", HttpStatus.OK.value(), "OK");

         }
      } catch (Exception e) {
         return new MessageResponse("Order Gagal Diupdate", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   @Transactional
   public MessageResponse delete(int orderId) {
      try {
         // delete
         Optional<OrderModel> orderModelOptional = orderRepository.findById(orderId);
         log.info("orderModelOptional: " + orderModelOptional);

         if (!orderModelOptional.isPresent()) {
            return new MessageResponse("Order Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         } else {
            OrderModel orderModel = orderModelOptional.get();
            orderRepository.delete(orderModel);
            return new MessageResponse("Order Berhasil Dihapus", HttpStatus.OK.value(), "OK");
         }
      } catch (Exception e) {
         return new MessageResponse("Order Gagal Dihapus", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   // get all order
   public ResponseEntity<Object> getAllOrder(Pageable pageable) {
      ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
      try {
         Page<OrderModel> orderModelList = orderRepository.findAll(pageable);

         List<DaftarOrderDTO> response = orderModelList.stream().map(orderModel -> {
            DaftarOrderDTO daftarOrderDTO = new DaftarOrderDTO();
            daftarOrderDTO.setOrderId(orderModel.getOrderId());
            daftarOrderDTO.setOrderCode(orderModel.getOrderCode());
            if (orderModel.getOrderDate() != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String formattedOrderDate = sdf.format(orderModel.getOrderDate());
               daftarOrderDTO.setOrderDate(formattedOrderDate);
            } else {
               daftarOrderDTO.setOrderDate(null);
            }
            DaftarCustomersDTO customerDTO = new DaftarCustomersDTO();
            CustomerModel customer = orderModel.getCustomer();
            customerDTO.setCustomerId(customer.getCustomerId());
            customerDTO.setCustomerName(customer.getCustomerName());
            customerDTO.setCustomerAddress(customer.getCustomerAddress());
            customerDTO.setCustomerCode(customer.getCustomerCode());
            customerDTO.setCustomerPhone(customer.getCustomerPhone());
            customerDTO.setIsActive(customer.getIsActive());
            if (customer.getLastOrderDate() != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String formattedLastOrderDate = sdf.format(customer.getLastOrderDate());
               customerDTO.setLastOrderDate(formattedLastOrderDate);
            } else {
               customerDTO.setLastOrderDate(null);
            }
            daftarOrderDTO.setCustomer(customerDTO);
            DaftarItemDTO itemDTO = new DaftarItemDTO();
            ItemModel item = orderModel.getItem();
            itemDTO.setItemId(item.getItemId());
            itemDTO.setItemName(item.getItemName());
            itemDTO.setItemCode(item.getItemsCode());
            itemDTO.setStock(item.getStock());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setIsAvailable(item.getIsAvailable());
            itemDTO.setLastReStock(item.getLastReStock());
            daftarOrderDTO.setItem(itemDTO);
            daftarOrderDTO.setQuantity(orderModel.getQuantity());
            daftarOrderDTO.setTotalPrice(orderModel.getTotalPrice());
            return daftarOrderDTO;
         }).collect(Collectors.toList());

         responseBodyDTO.setTotal(orderRepository.count());
         responseBodyDTO.setData(response);
         responseBodyDTO.setMessage("Daftar Order Berhasil Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.OK.value());
         responseBodyDTO.setStatus(HttpStatus.OK.name());

         return ResponseEntity.status(HttpStatus.OK).body(responseBodyDTO);

      } catch (Exception e) {
         responseBodyDTO.setMessage("Daftar Order Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         responseBodyDTO.setStatus("ERROR");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBodyDTO);
      }
   }

   // get order by id
   public ResponseBodyDTO getOrderById(int orderId) {
      try {
         Optional<OrderModel> orderModelOptional = orderRepository.findById(orderId);
         if (!orderModelOptional.isPresent()) {
            ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
            responseBodyDTO.setMessage("Order Tidak Ditemukan");
            responseBodyDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseBodyDTO.setStatus("ERROR");
            return responseBodyDTO;
         } else {
            OrderModel orderModel = orderModelOptional.get();
            DaftarOrderDTO daftarOrderDTO = new DaftarOrderDTO();
            daftarOrderDTO.setOrderId(orderModel.getOrderId());
            daftarOrderDTO.setOrderCode(orderModel.getOrderCode());
            if (orderModel.getOrderDate() != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String formattedOrderDate = sdf.format(orderModel.getOrderDate());
               daftarOrderDTO.setOrderDate(formattedOrderDate);
            } else {
               daftarOrderDTO.setOrderDate(null);
            }
            DaftarCustomersDTO customerDTO = new DaftarCustomersDTO();
            CustomerModel customer = orderModel.getCustomer();
            customerDTO.setCustomerId(customer.getCustomerId());
            customerDTO.setCustomerName(customer.getCustomerName());
            customerDTO.setCustomerAddress(customer.getCustomerAddress());
            customerDTO.setCustomerCode(customer.getCustomerCode());
            customerDTO.setCustomerPhone(customer.getCustomerPhone());
            customerDTO.setIsActive(customer.getIsActive());
            if (customer.getLastOrderDate() != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String formattedLastOrderDate = sdf.format(customer.getLastOrderDate());
               customerDTO.setLastOrderDate(formattedLastOrderDate);
            } else {
               customerDTO.setLastOrderDate(null);
            }
            daftarOrderDTO.setCustomer(customerDTO);
            DaftarItemDTO itemDTO = new DaftarItemDTO();
            ItemModel item = orderModel.getItem();
            itemDTO.setItemId(item.getItemId());
            itemDTO.setItemName(item.getItemName());
            itemDTO.setItemCode(item.getItemsCode());
            itemDTO.setStock(item.getStock());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setIsAvailable(item.getIsAvailable());
            itemDTO.setLastReStock(item.getLastReStock());
            daftarOrderDTO.setItem(itemDTO);
            daftarOrderDTO.setQuantity(orderModel.getQuantity());
            daftarOrderDTO.setTotalPrice(orderModel.getTotalPrice());
            ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
            responseBodyDTO.setTotal(1);
            responseBodyDTO.setData(daftarOrderDTO);
            responseBodyDTO.setMessage("Order Berhasil Ditemukan");
            responseBodyDTO.setStatusCode(HttpStatus.OK.value());
            responseBodyDTO.setStatus("OK");
            return responseBodyDTO;
         }
      } catch (Exception e) {
         ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
         responseBodyDTO.setMessage("Order Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         responseBodyDTO.setStatus("ERROR");

         return responseBodyDTO;
      }
   }

   // generate order code
   private String generateOrderCode(int customerId, int orderId) {
      int random = (int) (Math.random() * 900) + 100;
      int calculate = customerId + orderId + random;
      return "ORD" + calculate;
   }

}
