package id.synrgy.travimate.service;

import net.sf.jasperreports.engine.JRException;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
    byte[] generateTicketReport(UUID orderID, String format) throws JRException;


    byte[] generateAirlineSalesReport(String iataCode, String format, String periode,
                                      Integer month,
                                      Integer year,
                                      LocalDate week,
                                      LocalDate startDate, LocalDate endDate) throws JRException;
}