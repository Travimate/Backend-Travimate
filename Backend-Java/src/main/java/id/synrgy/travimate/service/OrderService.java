package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.model.Orders;

import java.util.UUID;

public interface OrderService {
    Object placeOrder(String username, OrderRequestDTO orderDTO);

    Object cancelOrder(UUID orderID);

    Object payOrder(UUID orderID);

    Orders findOrder(UUID orderID);
}
