package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.OrderRequestDTO;

public interface OrderService {
    Object createOrder(OrderRequestDTO orderDTO);
}
