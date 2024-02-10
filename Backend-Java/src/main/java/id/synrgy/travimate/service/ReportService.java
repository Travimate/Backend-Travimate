package id.synrgy.travimate.service;

import net.sf.jasperreports.engine.JRException;

import java.util.UUID;

public interface ReportService {
    byte[] generateReport(UUID orderID, String format) throws JRException;
}