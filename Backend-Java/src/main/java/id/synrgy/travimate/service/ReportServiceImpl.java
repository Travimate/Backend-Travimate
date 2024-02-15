package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.report.FlightReport;
import id.synrgy.travimate.dto.report.PassengerReport;
import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.*;
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

    @Autowired
    ReportServiceImpl(OrderService orderService,
                      UserRepository userRepository){
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @Override
    public byte[] generateTicketReport(UUID orderID, String format) throws JRException {

        Orders orders = orderService.findOrder(orderID);

        Map<String,Object> parameter =  new HashMap<>();
        parameter.put("bookingCode", orders.getBookingID());
        parameter.put("airlineBookingCode", orders.getPnrCode());
        parameter.put("flightDataSource", flightDataSource(orders));
        parameter.put("passengerDataSource", passengerDataSource(orders));
        parameter.put("flightReport", getFlightReport());
        parameter.put("iconUrl", getSource("/templates/icon/travimate.png"));
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

    private JasperPrint createJasperPrint(Map<String, Object> parameter) throws JRException {

        InputStream templateStream = getClass().getResourceAsStream("/templates/eticket.jrxml");

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

    private InputStream getSource(String source){
        return getClass().getResourceAsStream(source);
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

//    @Override
//    public byte[] generateAirlineSalesReport(UUID orderID, String format) throws JRException {
//
//        Orders orders = orderService.findOrder(orderID);
//
//        Map<String,Object> parameter =  new HashMap<>();
//        parameter.put("bookingCode", orders.getBookingID());
//        parameter.put("airlineBookingCode", orders.getPnrCode());
//        parameter.put("flightDataSource", flightDataSource(orders));
//        parameter.put("passengerDataSource", passengerDataSource(orders));
//        parameter.put("flightReport", getFlightReport());
//        JasperPrint jasperPrint = createJasperPrint(parameter);
//
//        return exportReport(jasperPrint, format);
//    }

//    @Override
//    public byte[] generateReport(UUID merchantId, String username,
//                                 RevenueRequestDTO requestDTO,
//                                 String format) throws JRException {
//
//        List<OrderDetail> orderDetails = new ArrayList<>();
//        Users users = userService.getUsersByUsername(username);
//        UUID userId = users.getId();
//        String request = requestDTO.getRequestBy();
//        String periodeInfo = "";
//
//        if(request.equals("yearly")) {
//            orderDetails = orderDetailRepository.findOrderDetailsByYear(
//                    merchantId, userId,
//                    requestDTO.getYear());
//            periodeInfo = "tahun "+ requestDTO.getYear();
//        }
//        else if (request.equals("monthly")) {
//            orderDetails = orderDetailRepository.findOrderDetailsByMonth(
//                    merchantId, userId,
//                    requestDTO.getMonth(),
//                    requestDTO.getYear());
//            periodeInfo = "bulan " + Month.of(requestDTO.getMonth()) + requestDTO.getYear();
//        }
//        else if (request.equals("weekly")) {
//            LocalDate startDate = requestDTO.getStartDate().with(DayOfWeek.MONDAY);
//            LocalDate endDate = startDate.plusDays(6);
//            orderDetails = orderDetailRepository.findOrderDetailsByCustomDate(
//                    merchantId, userId,
//                    startDate, endDate);
//            periodeInfo = getRangeDate(startDate, endDate)+startDate.getMonth()+" "+startDate.getYear();
//        }
//        else if (request.equals("custom")) {
//            orderDetails = orderDetailRepository.findOrderDetailsByCustomDate(
//                    merchantId, userId,
//                    requestDTO.getStartDate(),
//                    requestDTO.getEndDate());
//            periodeInfo = requestDTO.getStartDate()+" sampai "+requestDTO.getEndDate();
//        }
//        MerchantDTO merchant = merchantService.getMerchantById(merchantId);
//        parameter.put("merchantName", merchant.getMerchantName());
//        parameter.put("merchantLoc", merchant.getMerchantLocation());
//        parameter.put("periodeInfo", periodeInfo);
//        parameter.put("sellerName", users.getFirstName()+" "+users.getLastName());
//        List<InvoiceReportDTO> reportData = convertToReportDTO(orderDetails);
//        return generateJasperReport(reportData, format);
//    }
//
//    private List<InvoiceReportDTO> convertToReportDTO(List<OrderDetail> orderDetails) {
//        List<InvoiceReportDTO> reportData = new ArrayList<>();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH.mm");
//        for (OrderDetail orderDetail : orderDetails) {
//            InvoiceReportDTO dto = new InvoiceReportDTO();
//            dto.setProductName(orderDetail.getProduct().getProduct_name());
//            dto.setOrderTime(orderDetail.getOrders().getOrder_time().format(formatter));
//            dto.setTotalOrder(orderDetail.getQuantity());
//            dto.setTotalPrice(orderDetail.getTotal_price());
//            reportData.add(dto);
//        }
//        return reportData;
//    }




}
