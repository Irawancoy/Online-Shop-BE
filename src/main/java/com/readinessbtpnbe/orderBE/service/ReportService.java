package com.readinessbtpnbe.orderBE.service;

import com.readinessbtpnbe.orderBE.model.OrderModel;
import com.readinessbtpnbe.orderBE.repository.OrderRepository;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.readinessbtpnbe.orderBE.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;

@Service
public class ReportService {

   @Autowired
   private OrderRepository orderRepository;

   public MessageResponse generateReport(String format) throws FileNotFoundException, JRException {
      try {

         // auto downnload ke folder C:\Users\irawan\Downloads\downloadFile
         List<OrderModel> orders = orderRepository.findAll();
         // load file and compile it
         File file = ResourceUtils.getFile("classpath:orders.jrxml");
         JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
         JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);
         Map<String, Object> parameters = new HashMap<>();
         parameters.put("createdBy", "BTPN");
         JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
         // tanggal saat ini
         String date = java.time.LocalDate.now().toString();
         // tanggal saat ini pada nama file
         String path = "C:\\Users\\irawan\\Downloads\\downloadFile\\orders_" + date;
         if (format.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + ".html");
         }
         if (format.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + ".pdf");
         }
         return new MessageResponse("Report has been generated", HttpStatus.OK.value(), "OK");
      } catch (Exception e) {
         // TODO: handle exception
         return new MessageResponse("Error", HttpStatus.BAD_REQUEST.value(), "BAD REQUEST");
      }

   }
}