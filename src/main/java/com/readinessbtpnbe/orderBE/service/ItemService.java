package com.readinessbtpnbe.orderBE.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.readinessbtpnbe.orderBE.dto.request.CreateItemRequest;
import com.readinessbtpnbe.orderBE.dto.request.UpdateItemRequest;
import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import com.readinessbtpnbe.orderBE.dto.response.ResponseBodyDTO;
import com.readinessbtpnbe.orderBE.model.ItemModel;
import com.readinessbtpnbe.orderBE.repository.ItemRepository;
import com.readinessbtpnbe.orderBE.dto.response.DaftarItemDTO;

@Service
public class ItemService {

   @Autowired
   private ItemRepository itemRepository;

   @Transactional
   public MessageResponse create(CreateItemRequest request) {
      try {
         String itemCode = "I" + (int) (Math.random() * 1000);
         // create
         ItemModel itemModel = ItemModel.builder().itemName(request.getItemName()).itemsCode(itemCode)
               .stock(request.getStock()).price(request.getPrice()).isAvailable(request.getIsAvailable())
               .lastReStock(request.getLastReStock()).build();
         itemRepository.save(itemModel);
         return new MessageResponse("Item " + request.getItemName() + " Berhasil Ditambahkan", HttpStatus.OK.value(),
               "OK");
      } catch (Exception e) {
         return new MessageResponse("Item Gagal Ditambahkan", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   @Transactional
   public MessageResponse update(UpdateItemRequest request) {
      try {
         // update
         Optional<ItemModel> itemModelOptional = itemRepository.findById(request.getItemId());
         if (!itemModelOptional.isPresent()) {
            return new MessageResponse("Item Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         } else {
            ItemModel itemModel = itemModelOptional.get();
            // jika request null atau empty maka jangan dioperasikan
            if (request.getItemName() != null && !request.getItemName().isEmpty()) {
               itemModel.setItemName(request.getItemName());
            }
            if (request.getItemCode() != null && !request.getItemCode().isEmpty()) {
               itemModel.setItemsCode(request.getItemCode());
            }
            if (request.getStock() != 0) {
               itemModel.setStock(request.getStock());
            }
            if (request.getPrice() != 0) {
               itemModel.setPrice(request.getPrice());
            }
            if (request.getIsAvailable() != 0) {
               itemModel.setIsAvailable(request.getIsAvailable());
            }

            if (request.getLastReStock() != null) {
               itemModel.setLastReStock(request.getLastReStock());
            }
            itemRepository.save(itemModel);
            return new MessageResponse("Item Berhasil Diupdate", HttpStatus.OK.value(),
                  "OK");
         }
      } catch (Exception e) {
         return new MessageResponse("Item Gagal Diupdate", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   // delete item
   @Transactional
   public MessageResponse delete(int itemId) {
      try {
         Optional<ItemModel> itemModelOptional = itemRepository.findById(itemId);
         if (!itemModelOptional.isPresent()) {
            return new MessageResponse("Item Tidak Ditemukan", HttpStatus.NOT_FOUND.value(), "ERROR");
         } else {
            itemRepository.deleteById(itemId);
            return new MessageResponse("Item Berhasil Dihapus", HttpStatus.OK.value(), "OK");
         }
      } catch (Exception e) {
         return new MessageResponse("Item Gagal Dihapus", HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR");
      }
   }

   // get all item
   public ResponseEntity<Object> getAllItem(Pageable pageable) {
      ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
      try {
         Page<ItemModel> itemModelList = itemRepository.findAll(pageable);
         List<DaftarItemDTO> response = itemModelList.stream().map(itemModel -> {
            DaftarItemDTO daftarItemDTO = new DaftarItemDTO();
            daftarItemDTO.setItemId(itemModel.getItemId());
            daftarItemDTO.setItemName(itemModel.getItemName());
            daftarItemDTO.setItemCode(itemModel.getItemsCode());
            daftarItemDTO.setStock(itemModel.getStock());
            daftarItemDTO.setPrice(itemModel.getPrice());
            daftarItemDTO.setIsAvailable(itemModel.getIsAvailable());
            daftarItemDTO.setLastReStock(itemModel.getLastReStock());
            return daftarItemDTO;
         }).collect(Collectors.toList());

         responseBodyDTO.setTotal(itemRepository.count());
         responseBodyDTO.setData(response);
         responseBodyDTO.setMessage("Daftar Item Berhasil Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.OK.value());
         responseBodyDTO.setStatus(HttpStatus.OK.name() );
         return ResponseEntity.status(HttpStatus.OK).body(responseBodyDTO);

      } catch (Exception e) {
         responseBodyDTO.setMessage("Daftar Item Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         responseBodyDTO.setStatus("ERROR");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBodyDTO);
      }
   }

   // get item by id
   public ResponseBodyDTO getItemById(int itemId) {
      try {
         Optional<ItemModel> itemModelOptional = itemRepository.findById(itemId);
         if (!itemModelOptional.isPresent()) {
            ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
            responseBodyDTO.setMessage("Item Tidak Ditemukan");
            responseBodyDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseBodyDTO.setStatus("ERROR");
            return responseBodyDTO;
         } else {
            ItemModel itemModel = itemModelOptional.get();
            DaftarItemDTO daftarItemDTO = new DaftarItemDTO();
            daftarItemDTO.setItemId(itemModel.getItemId());
            daftarItemDTO.setItemName(itemModel.getItemName());
            daftarItemDTO.setItemCode(itemModel.getItemsCode());
            daftarItemDTO.setStock(itemModel.getStock());
            daftarItemDTO.setPrice(itemModel.getPrice());
            daftarItemDTO.setIsAvailable(itemModel.getIsAvailable());
            daftarItemDTO.setLastReStock(itemModel.getLastReStock());

            ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
            responseBodyDTO.setTotal(1);
            responseBodyDTO.setData(daftarItemDTO);
            responseBodyDTO.setMessage("Item Berhasil Ditemukan");
            responseBodyDTO.setStatusCode(HttpStatus.OK.value());
            responseBodyDTO.setStatus("OK");
            return responseBodyDTO;
         }
      } catch (Exception e) {
         ResponseBodyDTO responseBodyDTO = new ResponseBodyDTO();
         responseBodyDTO.setMessage("Item Gagal Ditemukan");
         responseBodyDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
         responseBodyDTO.setStatus("ERROR");
         return responseBodyDTO;
      }
   }

}
