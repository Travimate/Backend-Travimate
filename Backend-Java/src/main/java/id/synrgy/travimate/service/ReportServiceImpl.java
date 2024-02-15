package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.report.AirlineRevenueReport;
import id.synrgy.travimate.dto.report.FlightReport;
import id.synrgy.travimate.dto.report.PassengerReport;
import id.synrgy.travimate.model.*;
import id.synrgy.travimate.repository.OrderRepository;
import id.synrgy.travimate.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    ReportServiceImpl(OrderService orderService,
                      UserRepository userRepository,
                      OrderRepository orderRepository){
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public byte[] generateTicketReport(UUID orderID, String format) throws JRException {

        Orders orders = orderService.findOrder(orderID);

        Map<String,Object> parameter =  new HashMap<>();
        parameter.put("bookingCode", orders.getBookingID());
        parameter.put("airlineBookingCode", orders.getPnrCode());
        parameter.put("flightDataSource", flightDataSource(orders));
        parameter.put("passengerDataSource", passengerDataSource(orders));
        parameter.put("iconUrl", getSource("/templates/icon/travimate.png"));

        JasperPrint jasperPrint = createJasperPrint(parameter, "/templates/eticket.jrxml");

        return exportReport(jasperPrint, format);
    }

    private JRBeanCollectionDataSource flightDataSource(Orders order){
        List<FlightReport> flightDataset = new LinkedList<>();

        List<Flight> flightList = order.getFlightList();
        for (Flight flight : flightList){
            FlightReport flightReport = new FlightReport();
            flightReport.setAirlineName(flight.getAirline().getAirline_name());
            flightReport.setFlightClass(flight.getFlightClass().toString());
            flightReport.setImageUrl(flight.getAirline().getImageUrl());
            flightReport.setDep(flight.getDep().getIata_code());
            flightReport.setArr(flight.getArr().getIata_code());
            flightReport.setDepTime(flight.getDeparture_time());
            flightReport.setArrTime(flight.getArrival_time());

            LocalTime flightTime = flight.getFlight_time();
            long hours = flightTime.getHour();
            long minutes = flightTime.getMinute();

            String flightTimeString = String.format("%d jam %d menit", hours, minutes);
            flightReport.setFlightTime(flightTimeString);
            flightReport.setConnector(getSource("/templates/icon/connector.png"));

            flightDataset.add(flightReport);
        }

        return new JRBeanCollectionDataSource(flightDataset);
    }

    private JRBeanCollectionDataSource passengerDataSource(Orders order){
        List<PassengerReport> passengerDataset = new LinkedList<>();

        int passengerNumber = 1;
        List<Passenger> passengerList = order.getPassengerList();
        List<Flight> flightList = order.getFlightList();
        for (Passenger passenger : passengerList){
            PassengerReport passReport = new PassengerReport();
            passReport.setNo(passengerNumber);
            passengerNumber++;
            passReport.setFirstName(passenger.getFirstName());
            passReport.setLastName(passenger.getLastName());
            passReport.setType(passenger.getType().toString());
            passReport.setTicket(passenger.getTicketId());
            passReport.setBaggage("7 kg, 20 kg");
            passReport.setRoute(buildFlightDetails(flightList));

            passengerDataset.add(passReport);
        }

        return new JRBeanCollectionDataSource(passengerDataset);
    }

    private JasperPrint createJasperPrint(Map<String, Object> parameter, String templateSource) throws JRException {

        InputStream templateStream = getClass().getResourceAsStream(templateSource);

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

    private InputStream getSource(String source){
        return getClass().getResourceAsStream(source);
    }

    public static String buildFlightDetails(List<Flight> flightList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Flight flight : flightList) {
            stringBuilder.append(flight.getFlightNumber()).append("\n");
            stringBuilder.append(flight.getDep().getIata_code()).append(" - ").append(flight.getArr().getIata_code()).append("\n");
//            stringBuilder.append(getAirportDetails(flight.getDep())).append(" - ").append(getAirportDetails(flight.getArr())).append("\n");
        }
        return stringBuilder.toString();
    }

    private static String getAirportDetails(Airport airport) {
        return airport.getAirport_name() + " (" + airport.getIata_code() + ")";
    }

    @Override
    public byte[] generateAirlineSalesReport(String iataCode, String format,
                                             String periode, Integer month,
                                             Integer year, LocalDate week,
                                             LocalDate startDate, LocalDate endDate) throws JRException {

        Map<String,Object> parameter =  new HashMap<>();
        parameter = generateReport(parameter, iataCode, periode, month, year, week, startDate, endDate);

        JasperPrint jasperPrint = createJasperPrint(parameter, "/templates/airline.jrxml");

        return exportReport(jasperPrint, format);
    }


    public Map<String, Object> generateReport(Map<String, Object> parameter, String iataCode, String periode,
                                              Integer month, Integer year, LocalDate week, LocalDate startDate, LocalDate endDate) throws JRException {

        List<Flight> flightList = new ArrayList<>();
        String periodeInfo = "";

        if(periode.equals("yearly")) {
            flightList = orderRepository.findFlightsByAirlineAndYear(iataCode, year);
            periodeInfo = "tahun "+ year;
        }
        else if (periode.equals("monthly")) {
            flightList = orderRepository.findFlightsByAirlineAndMonthAndYear(iataCode, month, year);
            periodeInfo = "bulan " + Month.of(month) + year;
        }
        else if (periode.equals("weekly")) {
            LocalDate startDateW = week.with(DayOfWeek.MONDAY);
            LocalDate endDateW = startDate.plusDays(6);
            flightList = orderRepository.findFlightsByAirlineAndPeriod(iataCode,
                    startDateW, endDateW);
            periodeInfo = getRangeDate(startDateW, endDateW)+startDate.getMonth()+" "+startDateW.getYear();
        }
        else if (periode.equals("custom")) {
            flightList = orderRepository.findFlightsByAirlineAndPeriod(
                    iataCode, startDate, endDate);
            periodeInfo = startDate+" sampai "+endDate;
        }

        Airline airline = flightList.stream()
                .map(Flight::getAirline)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (airline != null) {
            parameter.put("airlineIconUrl", airline.getImageUrl());
            parameter.put("airlineName", airline.getAirline_name());
        }
        parameter.put("iconUrl", getSource("/templates/icon/travimate.png"));
        parameter.put("dataOrder", dataOrderSource(flightList));
        parameter.put("periodeInfo", periodeInfo);
        return parameter;
    }

    private Object dataOrderSource(List<Flight> flightList) {
        List<AirlineRevenueReport> airlineRevenueReports = new LinkedList<>();

        for (Flight flight : flightList){
            AirlineRevenueReport airlineReport = new AirlineRevenueReport();
            airlineReport.setFlightNumber(flight.getFlightNumber());
            airlineReport.setOrderTime(buildOrderTime(flight.getOrders()));
            airlineReport.setStringTotalOrder(buildTotalOrder(flight.getOrders()));
            airlineReport.setStringTotalAmount(buildTotalAmount(flight.getOrders()));
            airlineReport.setTotalAmount(sumTotalAmount(flight.getOrders()));
            airlineReport.setTotalOrder(getTotalPassengerCount(flight.getOrders()));
            airlineRevenueReports.add(airlineReport);
        }
        return new JRBeanCollectionDataSource(airlineRevenueReports);
    }

    public int getTotalPassengerCount(List<Orders> ordersList) {
        int totalPassengerCount = 0;
        for (Orders order : ordersList) {
            int passengerCount = order.getPassengerList().size();
            totalPassengerCount += passengerCount;
        }

        return totalPassengerCount;
    }

    private String getRangeDate(LocalDate startDate, LocalDate endDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return startDate.format(formatter)+"-"+endDate.format(formatter)+" ";
    }

    private Integer sumTotalAmount(List<Orders> ordersList) {
        return (int) ordersList.stream().mapToLong(Orders::getAmount).sum();
    }

    private String buildTotalAmount(List<Orders> ordersList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Orders orders : ordersList) {
            stringBuilder.append(orders.getAmount()).append("\n");
        }
        return stringBuilder.toString();
    }

    private String buildTotalOrder(List<Orders> ordersList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Orders orders : ordersList) {
            stringBuilder.append(orders.getPassengerList().size()).append("\n");
        }
        return stringBuilder.toString();
    }

    public String buildOrderTime(List<Orders> ordersList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Orders orders : ordersList) {
            stringBuilder.append(orders.getBookedDate()).append("\n");
        }
        return stringBuilder.toString();
    }

}
