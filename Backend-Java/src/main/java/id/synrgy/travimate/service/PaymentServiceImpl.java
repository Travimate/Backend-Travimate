package id.synrgy.travimate.service;

import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.Orders;
import id.synrgy.travimate.model.Payment;
import id.synrgy.travimate.repository.PaymentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final ReportService reportService;
    private final JavaMailSender javaMailSender;


    @Autowired
    PaymentServiceImpl(PaymentRepository paymentRepository,
                       OrderService orderService,
                       ReportService reportService,
                       JavaMailSender javaMailSender){
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.reportService = reportService;
        this.javaMailSender = javaMailSender;
    }

    @SneakyThrows
    @Override
    public Object makePayment(UUID orderID, String method) {

        //proses gateway dll
        Orders orders = orderService.findOrder(orderID);
        Payment payment = orders.getPayment();
        payment.setMethod(Payment.PaymentMethod.valueOf(method.toUpperCase()));
        payment.setAmount(orders.getAmount());
        payment.setConfirmed(true);
        paymentRepository.save(payment);
        byte[] ticketReportBytes = reportService.generateTicketReport(orderID, "pdf");

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(msg, true);
            helper.setTo(orders.getBookedMail());
            helper.setSubject("Travimate E-ticket");
            ByteArrayDataSource dataSource = new ByteArrayDataSource(ticketReportBytes, "application/pdf");
            helper.addAttachment("ticket_report.pdf", dataSource);
            javaMailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        }

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
