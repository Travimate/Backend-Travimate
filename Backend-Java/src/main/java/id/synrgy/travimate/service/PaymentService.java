package id.synrgy.travimate.service;

import java.util.UUID;

public interface PaymentService {

    Object makePayment(UUID orderID, String method);

    Object checkStatus(UUID paymentID);
}
