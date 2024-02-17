package id.synrgy.travimate.controller;

import id.synrgy.travimate.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;
    @Autowired
    ReportController(ReportService reportService){
        this.reportService = reportService;
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

    @GetMapping("/airline-revenue")
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
