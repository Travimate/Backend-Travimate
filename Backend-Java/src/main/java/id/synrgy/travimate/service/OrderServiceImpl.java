package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.dto.request.PassengerRequestDTO;
import id.synrgy.travimate.dto.response.FlightDTO;
import id.synrgy.travimate.dto.response.OrderDTO;
import id.synrgy.travimate.dto.response.PassengerDTO;
import id.synrgy.travimate.exception.ExistingResourceFoundException;
import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.*;
import id.synrgy.travimate.repository.OrderRepository;
import id.synrgy.travimate.repository.PassengerRepository;
import id.synrgy.travimate.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final FlightService flightService;

    @Autowired
    OrderServiceImpl(OrderRepository orderRepository,
                     PassengerRepository passengerRepository,
                     UserService userService,
                     FlightService flightService,
                     PaymentRepository paymentRepository){
        this.orderRepository = orderRepository;
        this.passengerRepository = passengerRepository;
        this.userService = userService;
        this.flightService = flightService;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Object placeOrder(String username, OrderRequestDTO orderDTO) {

        Orders orders = new Orders();
        orders.setBookingID(generateBookingId());
        orders.setPnrCode(generatePNR());
        orders.setUsers(userService.findByUsername(username));
        orders.setBookedBy(orderDTO.getBookedBy());
        orders.setBookedMail(orderDTO.getBookedMail());
        orders.setBookedDate(LocalDate.now());
        orders.setFlightList(addFlightList(orderDTO.getFlightID()));
        orders.setPassengerList(addPassengerList(orderDTO.getPassengerList(),
                addFlightList(orderDTO.getFlightID())));
        orders.setAmount(sumAmount(orderDTO.getFlightDataID(), orders.getPassengerList()));
        orders.setPayment(addNewPayment());
        orders.setPaid(false);
        orderRepository.save(orders);

        setOrderForPassenger(orders);
        return mapToOrderDTO(orders);
    }

    private void setOrderForPassenger(Orders orders) {
        List<Passenger> passengerList = orders.getPassengerList();
        passengerList.forEach(passenger -> passenger.setOrders(orders));
        passengerRepository.saveAll(passengerList);
    }
    private Payment addNewPayment() {
        Payment payment = new Payment();
        payment.setConfirmed(false);
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Object cancelOrder(UUID orderID) {
        Orders orders = findOrder(orderID);
        if(orders.getCompleted()==null){
            orders.setCompleted(false);
            orderRepository.save(orders);
            return mapToOrderDTO(orders);
        } else {
            throw new ExistingResourceFoundException("data sudah terkonfirmasi : " + orders.getCompleted());
        }
    }

    @Override
    @Transactional
    public Object payOrder(UUID orderID, boolean isPaid) {

        Orders orders = findOrder(orderID);
        if(isPaid){
            orders.setPaid(true);
            reduceFlightStock(orders);
        }
        orders.setCompleted(true);
        orderRepository.save(orders);
        return mapToOrderDTO(orders);
    }

    private void reduceFlightStock(Orders orders) {
        List<Flight> flights = orders.getFlightList();
        int totalPassengerCount = orders.getPassengerList().size();

        for (Flight flight : flights) {
            int currentStock = flight.getStock();
            flight.setStock(currentStock - totalPassengerCount);
            flightService.save(flight);
        }
    }

    @Override
    public Orders findOrder(UUID orderID){
        return orderRepository.findById(orderID)
                .orElseThrow(()-> new ResourceNotFoundException(orderID));
    }

    @Override
    public Object history(String username) {
        Users users = userService.findByUsername(username);
        List<Orders> ordersList = orderRepository.findByUsers(users);
        return ordersList.stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Flight> addFlightList(List<UUID> flightID) {
        List<Flight> flightList = new LinkedList<>();
        for(UUID id : flightID){
            flightList.add(flightService.findFlightByID(id));
        }
        flightList.sort(Comparator
                .comparing(Flight::getDof)
                .thenComparing(Flight::getDeparture_time));
        return flightList;
    }

    private List<Passenger> addPassengerList(List<PassengerRequestDTO> passengerList,
                                             List<Flight> flightList) {
        List<Passenger> passengers = new LinkedList<>();
        List<String> ticketID = generateTicketId(passengerList.size());
        int index = 0;
        for (PassengerRequestDTO psDTO : passengerList) {
            String ticketId = ticketID.get(index);
            Flight flight = flightList.get(index % flightList.size());
            Passenger passenger = new Passenger();
            passenger.setGreeting(psDTO.getGreeting());
            passenger.setFirstName(psDTO.getFirstName());
            passenger.setLastName(psDTO.getLastName());
            passenger.setType(Passenger.PassengerType.valueOf(psDTO.getType().toUpperCase()));
            passenger.setTicketId(ticketId);
            passenger.setFlight(flight);
            passengerRepository.save(passenger);
            passengers.add(passenger);
            index++;
        }
        return passengers;
    }

    public OrderDTO mapToOrderDTO(Orders orders) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderID(orders.getOrderID());
        orderDTO.setUsername(orders.getUsers().getUsername());
        orderDTO.setBookingID(orders.getBookingID());
        orderDTO.setBookedBy(orders.getBookedBy());
        orderDTO.setBookedDate(orders.getBookedDate());
        orderDTO.setBookedMail(orders.getBookedMail());
        orderDTO.setPnrCode(orders.getPnrCode());
        orderDTO.setAmount(orders.getAmount());
        orderDTO.setCompleted(orders.getCompleted());
        orderDTO.setPaid(orders.getPaid());

        List<FlightDTO> flightDTOList = orders.getFlightList().stream()
                .map(flightService::mapToDTO)
                .collect(Collectors.toList());
        orderDTO.setFlightList(flightDTOList);

        List<PassengerDTO> passengerDTOList = orders.getPassengerList().stream()
                .map(this::mapToPassengerDTO)
                .collect(Collectors.toList());
        orderDTO.setPassengerList(passengerDTOList);

        return orderDTO;
    }
    public PassengerDTO mapToPassengerDTO(Passenger passenger) {
        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setId(passenger.getId());
        passengerDTO.setGreeting(passenger.getGreeting());
        passengerDTO.setFirstName(passenger.getFirstName());
        passengerDTO.setLastName(passenger.getLastName());
        passengerDTO.setTicketId(passenger.getTicketId());
        passengerDTO.setFlightNumber(passenger.getFlight().getFlightNumber());
        return passengerDTO;
    }

    private long sumAmount(UUID flightID, List<Passenger> passengerList) {
        FlightData flightData = flightService.findFlightDataByID(flightID);
        long adultFare = flightData.getBaseFare().getAdultBaseFare();
        long childFare = flightData.getBaseFare().getChildBaseFare();
        long totalAmount = 0;
        for (Passenger passenger : passengerList) {
            if (passenger.getType() == Passenger.PassengerType.ADULT) {
                totalAmount += adultFare;
            } else if (passenger.getType() == Passenger.PassengerType.CHILD) {
                totalAmount += childFare;
            }
        }
        return totalAmount;
    }

    private String generateBookingId(){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (Math.random() < 0.5) {
                result.append((char) (Math.random() * 26 + 'A'));
            } else {
                result.append((char) (Math.random() * 10 + '0'));
            }
        }
        return "TV" + result;
    }
    private String generatePNR() {
        return generateRandomDigits(7);
    }

    public List<String> generateTicketId(int count) {
        List<String> resultList = new ArrayList<>();
        String commonPrefix = generateRandomString(5);

        for (int i = 0; i < count; i++) {
            resultList.add(commonPrefix + generateRandomDigits(7));
        }

        return resultList;
    }

    private String generateRandomDigits(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) (Math.random() * 10 + '0'));
        }
        return result.toString();
    }

    private String generateRandomString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) (Math.random() * 26 + 'A'));
        }
        return result.toString();
    }
}
