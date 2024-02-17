package id.synrgy.travimate.controller;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.dto.response.ResponseHandler;
import id.synrgy.travimate.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {


    private final OrderService orderService;
    @Autowired
    OrderController(OrderService orderService){
        this.orderService = orderService;
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

}
