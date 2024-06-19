package com.readinessbtpnbe.orderBE.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.readinessbtpnbe.orderBE.dto.request.CreateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.request.UpdateCustomerRequest;
import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import com.readinessbtpnbe.orderBE.model.CustomerModel;
import com.readinessbtpnbe.orderBE.repository.CustomerRepository;

import com.readinessbtpnbe.orderBE.dto.response.DaftarCustomersDTO;
import com.readinessbtpnbe.orderBE.dto.response.ResponseBodyDTO;

import java.text.SimpleDateFormat;
import lib.minio.MinioSrvc;
import lib.minio.exception.MinioServiceException;

import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
public class CustomerService {

   @Autowired
   private CustomerRepository customerRepository;

   @Autowired
   private MinioSrvc minioSrvc;

   @Transactional
   public MessageResponse create(CreateCustomerRequest request, MultipartFile file) {
      try {
         String customerCode = "C" + (int) (Math.random() * 1000);
         String imageFileName;
         // upload file
         try {
            imageFileName = minioSrvc.uploadImageToMinio(request, file);
         } catch (Exception e) {
            String errorMessage = "Gagal upload file ke Minio";
            log.error(errorMessage, e);
            throw new MinioServiceException(errorMessage, e);
         }

         // create
         CustomerModel customerModel = CustomerModel.builder().customerName(request.getCustomerName())
               .customerAddress(request.getCustomerAddress()).customerCode(customerCode)
               .customerPhone(request.getCustomerPhone()).isActive(request.getIsActive()).pic(imageFileName).build();
         customerRepository.save(customerModel);
         return new MessageResponse("Customer " + request.getCustomerName() + " Berhasil Ditambahkan",
               HttpStatus.OK.value(), "OK");
      } catch (Exception e) {
         return new MessageResponse("Customer Gagal Ditambahkan", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   @Transactional
   public MessageResponse update(UpdateCustomerRequest request, MultipartFile file) {
      try {

         // upload file
         // String imageFileName;
         // try {
         //    imageFileName = minioSrvc.updateImageToMinio(request, file);
         // } catch (Exception e) {
         //    String errorMessage = "Gagal upload file ke Minio";
         //    log.error(errorMessage, e);
         //    throw new MinioServiceException(errorMessage, e);
         // }
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
            customerModel.setIsActive(request.getIsActive());
            // customerModel.setPic(imageFileName);

            if (file != null) {
               try {
                  String newImageFileName = minioSrvc.updateImageToMinio(request, file);
                  customerModel.setPic(newImageFileName);
               } catch (Exception e) {
                  // TODO: handle exception
                  String errorMessage = "Gagal upload file ke Minio";
                  throw new MinioServiceException(errorMessage, e);
               }
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
   public ResponseEntity<Object> getAllCustomer(Pageable pageable) {
      ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
      try {
         // get all customer
         Page<CustomerModel> customerModelList = customerRepository.findAll(pageable);

         List<DaftarCustomersDTO> response = customerModelList.stream().map(customerModel -> {
            DaftarCustomersDTO daftarCustomersDTO = new DaftarCustomersDTO();
            daftarCustomersDTO.setCustomerId(customerModel.getCustomerId());
            daftarCustomersDTO.setCustomerName(customerModel.getCustomerName());
            daftarCustomersDTO.setCustomerAddress(customerModel.getCustomerAddress());
            daftarCustomersDTO.setCustomerCode(customerModel.getCustomerCode());
            daftarCustomersDTO.setCustomerPhone(customerModel.getCustomerPhone());
            daftarCustomersDTO.setIsActive(customerModel.getIsActive());
            daftarCustomersDTO.setLastOrderDate(
       null
            );
            daftarCustomersDTO.setPic(getImageUrl(customerModel.getPic()));
            if (customerModel.getLastOrderDate() != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String formattedLastOrderDate = sdf.format(customerModel.getLastOrderDate());
               daftarCustomersDTO.setLastOrderDate(formattedLastOrderDate);
           } else {
               daftarCustomersDTO.setLastOrderDate(null);
           }
            return daftarCustomersDTO;

         }).collect(Collectors.toList());
     

         

         responseBodyDTO.setTotal(customerRepository.count());
         responseBodyDTO.setData(response);
         responseBodyDTO.setMessage("Daftar Customer Berhasil Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.OK.value());
         responseBodyDTO.setStatus(HttpStatus.OK.name());

         return ResponseEntity.status(HttpStatus.OK).body(responseBodyDTO);

      } catch (Exception e) {
         responseBodyDTO.setMessage("Daftar Customer Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         responseBodyDTO.setStatus("ERROR");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBodyDTO);
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
            daftarCustomersDTO.setPic(getImageUrl(customerModel.getPic()));
            if (customerModel.getLastOrderDate() != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String formattedLastOrderDate = sdf.format(customerModel.getLastOrderDate());
               daftarCustomersDTO.setLastOrderDate(formattedLastOrderDate);
            } else {
               daftarCustomersDTO.setLastOrderDate(null);
            }
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

   public String getImageUrl(String filename) {
      String url = "";
      if (filename != null) {
         url = minioSrvc.getPublicLink(filename);
      }
      return url;
   }
}
