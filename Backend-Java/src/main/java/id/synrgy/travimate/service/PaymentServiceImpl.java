package id.synrgy.travimate.service;

import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.Orders;
import id.synrgy.travimate.model.Payment;
import id.synrgy.travimate.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Autowired
    PaymentServiceImpl(PaymentRepository paymentRepository,
                       OrderService orderService){
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Override
    public Object makePayment(UUID orderID, String method) {

        //LOGIC PAYMENT
        Orders orders = orderService.findOrder(orderID);
        Payment payment = orders.getPayment();
        payment.setMethod(Payment.PaymentMethod.valueOf(method.toUpperCase()));
        payment.setAmount(orders.getAmount());
        payment.setConfirmed(true);
        paymentRepository.save(payment);

        orderService.payOrder(orderID);

        return payment;
    }

    @Override
    public Object checkStatus(UUID paymentID){
        Payment payment = paymentRepository.findById(paymentID)
                .orElseThrow(()-> new ResourceNotFoundException(paymentID));
        return payment.isConfirmed();
    }
}
