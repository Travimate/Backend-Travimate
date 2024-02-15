package id.synrgy.travimate.controller;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.dto.response.ResponseHandler;
import id.synrgy.travimate.service.OrderService;
import id.synrgy.travimate.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {


    private final OrderService orderService;
    private final ReportService reportService;
    @Autowired
    OrderController(OrderService orderService,
                    ReportService reportService){
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> placeOrder(@RequestBody OrderRequestDTO orderDTO,
                                             Principal principal){
        return ResponseHandler.generateResponseSuccess(
                orderService.placeOrder(principal.getName(),
                orderDTO));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Object> cancelOrder(@RequestParam UUID orderID){
        return ResponseHandler.generateResponseSuccess(orderService.cancelOrder(orderID));
    }

    @GetMapping("/history")
    public ResponseEntity<Object> history(Principal principal){
        return ResponseHandler.generateResponseSuccess(orderService.history(principal.getName()));
    }



    @GetMapping("/e-ticket")
    public ResponseEntity<byte[]> generateReport(@RequestParam UUID orderID,
                                                 @RequestParam String format) throws JRException {
        byte[] reportBytes = reportService
                .generateTicketReport(orderID, format);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report." + format);

        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/airline-report")
    public ResponseEntity<byte[]> generateReport(@RequestParam String iataCode,
                                                 @RequestParam String format,
                                                 @RequestParam String periode,
                                                 @RequestParam(required = false) Integer month,
                                                 @RequestParam(required = false) Integer year,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate week,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws JRException {
        byte[] reportBytes = reportService
                .generateAirlineSalesReport(iataCode,
                        format, periode, month, year, week, startDate, endDate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report." + format);

        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }
}
