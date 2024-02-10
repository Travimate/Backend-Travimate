package id.synrgy.travimate.controller;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.dto.response.ResponseHandler;
import id.synrgy.travimate.service.OrderService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {


    private final OrderService orderService;
    @Autowired
    OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequestDTO orderDTO){
        return ResponseHandler.generateResponseSuccess(orderService.createOrder(orderDTO));
    }
}
