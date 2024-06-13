package com.readinessbtpnbe.orderBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.readinessbtpnbe.orderBE.dto.request.CreateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.request.UpdateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import com.readinessbtpnbe.orderBE.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

   @Autowired
   private CustomerService customerService;

   // create customer
   @PostMapping("/create")
   public MessageResponse create(@Valid @RequestBody CreateCustomerRequest request) {
      return customerService.create(request);
   }

   // update customer
   @PutMapping(path = { "/update" }, consumes = { MediaType.APPLICATION_JSON_VALUE,
         MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<MessageResponse> update(@Valid @RequestPart("request") UpdateCustomerRequest request) {
      MessageResponse response = customerService.update(request);
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   // delete customer
   @PutMapping("/delete")
   public MessageResponse delete(@RequestParam int customerId) {
      return customerService.delete(customerId);
   }

   // get all customer
   @GetMapping("/get-all")
   public ResponseEntity<Object> getAll(
         @PageableDefault(page = 0, size = 8,sort = "customerId",direction = Direction.ASC) Pageable pageable) {
      return customerService.getAllCustomer(pageable);
   }

   // get customer by id
   @GetMapping("/get-by-id")
   public ResponseEntity<Object> getById(@RequestParam int customerId) {
      return ResponseEntity.ok(customerService.getCustomerById(customerId));
   }

}
