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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.readinessbtpnbe.orderBE.dto.request.CreateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.request.UpdateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import com.readinessbtpnbe.orderBE.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

   @Autowired
   private CustomerService customerService;

   // create customer
   @PostMapping(path = { "/create" }, consumes = { MediaType.APPLICATION_JSON_VALUE,
         MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<MessageResponse> create(@RequestPart("request") CreateCustomerRequest request,
         @RequestPart(value = "file", required = true) MultipartFile file) {
      MessageResponse response = customerService.create(request, file);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
   }

   // update customer
   @PutMapping(path = { "/update" }, consumes = {
         MediaType.APPLICATION_JSON_VALUE,
         MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<MessageResponse> update(@RequestPart("request") UpdateCustomerRequest request,
         @RequestPart(value = "file", required = false) MultipartFile file) {
      MessageResponse response = customerService.update(request, file);
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
