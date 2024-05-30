package com.readinessbtpnbe.orderBE.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;


@Data
public class CreateCustomerRequest {

   @NotEmpty(message = "Nama Customer tidak boleh kosong")
   private String customerName;
   @NotEmpty(message = "Alamat Customer tidak boleh kosong")
   private String customerAddress;
   @NotEmpty(message = "Kode Customer tidak boleh kosong")
   private String customerCode;
   @NotEmpty(message = "Nomor Telepon Customer tidak boleh kosong")
   private String customerPhone;
   private int isActive;
   @NotEmpty(message = "PIC Customer tidak boleh kosong")
   private String pic;
}
