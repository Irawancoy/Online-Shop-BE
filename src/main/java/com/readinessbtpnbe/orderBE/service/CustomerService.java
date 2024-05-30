package com.readinessbtpnbe.orderBE.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import com.readinessbtpnbe.orderBE.dto.request.CreateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.request.UpdateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import com.readinessbtpnbe.orderBE.model.CustomerModel;
import com.readinessbtpnbe.orderBE.repository.CustomerRepository;

import com.readinessbtpnbe.orderBE.dto.response.DaftarCustomersDTO;
import com.readinessbtpnbe.orderBE.dto.response.ResponseBodyDTO;

@Slf4j
@Service
public class CustomerService {

   @Autowired
   private CustomerRepository customerRepository;

   @Transactional
   public MessageResponse create(CreateCustomerRequest request) {
      try {
         // create
         CustomerModel customerModel = CustomerModel.builder().customerName(request.getCustomerName())
               .customerAddress(request.getCustomerAddress()).customerCode(request.getCustomerCode())
               .customerPhone(request.getCustomerPhone()).isActive(request.getIsActive()).pic(request.getPic()).build();
         customerRepository.save(customerModel);
         return new MessageResponse("Customer " + request.getCustomerName() + " Berhasil Ditambahkan",
               HttpStatus.OK.value(), "OK");
      } catch (Exception e) {
         return new MessageResponse("Customer Gagal Ditambahkan", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   @Transactional
   public MessageResponse update(UpdateCustomerRequest request) {
      try {
         // update
         Optional<CustomerModel> customerModelOptional = customerRepository.findById(request.getCustomerId());
         log.info("customerModelOptional: " + customerModelOptional);
         if (!customerModelOptional.isPresent()) {
            return new MessageResponse("Customer Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         } else {
            CustomerModel customerModel = customerModelOptional.get();
            // jika request null atau empty maka jangan dioperasikan
            if (request.getCustomerName() != null && !request.getCustomerName().isEmpty()) {
               customerModel.setCustomerName(request.getCustomerName());
            }
            if (request.getCustomerAddress() != null && !request.getCustomerAddress().isEmpty()) {
               customerModel.setCustomerAddress(request.getCustomerAddress());
            }
            if (request.getCustomerCode() != null && !request.getCustomerCode().isEmpty()) {
               customerModel.setCustomerCode(request.getCustomerCode());
            }
            if (request.getCustomerPhone() != null && !request.getCustomerPhone().isEmpty()) {
               customerModel.setCustomerPhone(request.getCustomerPhone());
            }
            if (request.getIsActive() != 0) {
               customerModel.setIsActive(request.getIsActive());
            }
            if (request.getPic() != null && !request.getPic().isEmpty()) {
               customerModel.setPic(request.getPic());
            }
            customerRepository.save(customerModel);
            return new MessageResponse("Customer  Berhasil Diupdate",
                  HttpStatus.OK.value(), "OK");

         }
      } catch (Exception e) {
         return new MessageResponse("Customer Gagal Diupdate", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   @Transactional
   public MessageResponse delete(int customerId) {
      try {
         // delete
         Optional<CustomerModel> customerModelOptional = customerRepository.findById(customerId);
         if (!customerModelOptional.isPresent()) {
            return new MessageResponse("Customer Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         } else {
            CustomerModel customerModel = customerModelOptional.get();
            customerRepository.delete(customerModel);
            return new MessageResponse("Customer Berhasil Dihapus", HttpStatus.OK.value(), "OK");
         }
      } catch (Exception e) {
         return new MessageResponse("Customer Gagal Dihapus", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   // get all customer
   public ResponseBodyDTO getAllCustomer() {
      try {
         // get all customer
         List<CustomerModel> customerModelList = customerRepository.findAll();

         List<DaftarCustomersDTO> response = customerModelList.stream().map(customerModel -> {
            DaftarCustomersDTO daftarCustomersDTO = new DaftarCustomersDTO();
            daftarCustomersDTO.setCustomerId(customerModel.getCustomerId());
            daftarCustomersDTO.setCustomerName(customerModel.getCustomerName());
            daftarCustomersDTO.setCustomerAddress(customerModel.getCustomerAddress());
            daftarCustomersDTO.setCustomerCode(customerModel.getCustomerCode());
            daftarCustomersDTO.setCustomerPhone(customerModel.getCustomerPhone());
            daftarCustomersDTO.setIsActive(customerModel.getIsActive());
            daftarCustomersDTO.setLastOrderDate(customerModel.getLastOrderDate());
            daftarCustomersDTO.setPic(customerModel.getPic());
            return daftarCustomersDTO;
         }).collect(Collectors.toList());

         ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
         responseBodyDTO.setTotal(response.size());
         responseBodyDTO.setData(response);
         responseBodyDTO.setMessage("Daftar Customer Berhasil Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.OK.value());
         responseBodyDTO.setStatus("OK");

         return responseBodyDTO;

      } catch (Exception e) {
         ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
         responseBodyDTO.setTotal(0);
         responseBodyDTO.setMessage("Daftar Customer Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         return responseBodyDTO;
      }
   }

   // get customer by id
   public ResponseBodyDTO getCustomerById(int customerId) {
      try {
         // get customer by id
         Optional<CustomerModel> customerModelOptional = customerRepository.findById(customerId);
         if (!customerModelOptional.isPresent()) {
            ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
            responseBodyDTO.setTotal(0);
            responseBodyDTO.setMessage("Customer Tidak Ditemukan");
            return responseBodyDTO;
         } else {
            CustomerModel customerModel = customerModelOptional.get();
            DaftarCustomersDTO daftarCustomersDTO = new DaftarCustomersDTO();
            daftarCustomersDTO.setCustomerId(customerModel.getCustomerId());
            daftarCustomersDTO.setCustomerName(customerModel.getCustomerName());
            daftarCustomersDTO.setCustomerAddress(customerModel.getCustomerAddress());
            daftarCustomersDTO.setCustomerCode(customerModel.getCustomerCode());
            daftarCustomersDTO.setCustomerPhone(customerModel.getCustomerPhone());
            daftarCustomersDTO.setIsActive(customerModel.getIsActive());
            daftarCustomersDTO.setLastOrderDate(customerModel.getLastOrderDate());
            daftarCustomersDTO.setPic(customerModel.getPic());

            ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
            responseBodyDTO.setTotal(1);
            responseBodyDTO.setData(daftarCustomersDTO);
            responseBodyDTO.setMessage("Customer Berhasil Ditemukan");
            responseBodyDTO.setStatusCode(HttpStatus.OK.value());
            responseBodyDTO.setStatus("OK");

            return responseBodyDTO;
         }
      } catch (Exception e) {
         ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
         responseBodyDTO.setMessage("Customer Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         responseBodyDTO.setStatus("ERROR");
         return responseBodyDTO;
      }
     
   }
}
