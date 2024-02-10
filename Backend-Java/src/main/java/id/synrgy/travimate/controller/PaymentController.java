package id.synrgy.travimate.controller;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.dto.response.ResponseHandler;
import id.synrgy.travimate.model.Payment;
import id.synrgy.travimate.service.PaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Hidden
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<Object> makePayment(@RequestParam UUID orderID, @RequestParam String method){
        return ResponseHandler.generateResponseSuccess(paymentService.makePayment(orderID, method));
    }

    @GetMapping("/status")
    public ResponseEntity<Object> checkStatusPayment (@RequestParam UUID paymentID){
        return ResponseHandler.generateResponseSuccess(paymentService.checkStatus(paymentID));
    }
}
