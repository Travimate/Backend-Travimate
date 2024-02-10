package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.report.FlightReport;
import id.synrgy.travimate.dto.report.PassengerReport;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.Flight;
import id.synrgy.travimate.model.Orders;
import id.synrgy.travimate.model.Passenger;
import id.synrgy.travimate.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @Autowired
    ReportServiceImpl(OrderService orderService,
                      UserRepository userRepository){
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @Override
    public byte[] generateReport(UUID orderID, String format) throws JRException {

        Orders orders = orderService.findOrder(orderID);

        Map<String,Object> parameter =  new HashMap<>();
        parameter.put("bookingCode", orders.getBookingID());
        parameter.put("airlineBookingCode", orders.getPnrCode());
        parameter.put("flightDataSource", flightDataSource(orders));
        parameter.put("passengerDataSource", passengerDataSource(orders));
        parameter.put("flightReport", getFlightReport());
        JasperPrint jasperPrint = createJasperPrint(parameter);

        return exportReport(jasperPrint, format);
    }

    private JRBeanCollectionDataSource flightDataSource(Orders order){
        List<FlightReport> flightDataset = new LinkedList<>();

        order.getFlightList().forEach(flight -> System.out.println("flight = "+ flight));
        List<Flight> flightList = order.getFlightList();
        for (Flight flight : flightList){
            FlightReport flightReport = new FlightReport();
            flightReport.setAirlineName(flight.getAirline().getAirline_name());
            flightReport.setFlightClass(flight.getFlightClass().toString());
            flightReport.setImageUrl(flight.getAirline().getImageUrl());

            flightDataset.add(flightReport);
        }

        return new JRBeanCollectionDataSource(flightDataset);
    }

    private JRBeanCollectionDataSource passengerDataSource(Orders order){
        List<PassengerReport> passengerDataset = new LinkedList<>();

        order.getPassengerList().forEach(passenger -> System.out.println("passenger = "+passenger));
        List<Passenger> passengerList = order.getPassengerList();
        List<Flight> flightList = order.getFlightList();
        for (Passenger passenger : passengerList){
            PassengerReport passReport = new PassengerReport();
            passReport.setNo(1);
            passReport.setFirstName(passenger.getFirstName());
            passReport.setLastName(passenger.getLastName());
            passReport.setType(passenger.getType().toString());
            passReport.setTicket(passenger.getTicketId());
            passReport.setBaggage(null);
            passReport.setRoute(buildFlightDetails(flightList));

            passengerDataset.add(passReport);
        }

        return new JRBeanCollectionDataSource(passengerDataset);
    }

    private JasperPrint createJasperPrint(Map<String, Object> parameter) throws JRException {

        InputStream templateStream = getClass().getResourceAsStream("/templates/ticket.jrxml");

        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        return JasperFillManager.fillReport(jasperReport, parameter, new JREmptyDataSource());
    }

    private byte[] exportReport(JasperPrint jasperPrint, String format) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            switch (format.toLowerCase()) {
                case "pdf":
                    JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                    break;
                case "html":
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, String.valueOf(outputStream));
                    break;
                case "csv":
                    //csv
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (JRException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private JasperReport getFlightReport(){
        InputStream templateStream = getClass().getResourceAsStream("/templates/flight-data.jrxml");
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(templateStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
        return jasperReport;
    }

    public static String buildFlightDetails(List<Flight> flightList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Flight flight : flightList) {
            stringBuilder.append(flight.getFlightNumber()).append("\n");
            stringBuilder.append(getAirportDetails(flight.getDep())).append(" - ").append(getAirportDetails(flight.getArr())).append("\n");
        }
        return stringBuilder.toString();
    }

    private static String getAirportDetails(Airport airport) {
        return airport.getAirport_name() + " (" + airport.getIata_code() + ")";
    }

}
