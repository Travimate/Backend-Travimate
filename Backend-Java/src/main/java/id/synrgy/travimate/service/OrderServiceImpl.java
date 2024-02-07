package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.OrderRequestDTO;
import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.Flight;
import id.synrgy.travimate.model.Order;
import id.synrgy.travimate.model.Passenger;
import id.synrgy.travimate.model.User;
import id.synrgy.travimate.repository.FlightRepository;
import id.synrgy.travimate.repository.OrderRepository;
import id.synrgy.travimate.repository.PassengerRepository;
import id.synrgy.travimate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    OrderServiceImpl(OrderRepository orderRepository,
                     PassengerRepository passengerRepository,
                     UserRepository userRepository,
                     FlightRepository flightRepository){
        this.orderRepository = orderRepository;
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public Object createOrder(OrderRequestDTO orderDTO) {

        Order order = new Order();
        order.setBookingID(generateBookingId());
        order.setpnrCode(generatePNR());
        order.setUser(findUserByID(orderDTO.getUserId()));
        order.setBookedBy(orderDTO.getBookedBy());
        order.setBookedMail(orderDTO.getBookedMail());
        order.setFlightList(addFlightList(orderDTO.getFlightID()));
        order.setPassengerList(addPassengerList(orderDTO.getPassengerList(),
                addFlightList(orderDTO.getFlightID())));
        orderRepository.save(order);
        return order;
    }

    private List<Flight> addFlightList(List<UUID> flightID) {
        List<Flight> flightList = new LinkedList<>();
        for(UUID id : flightID){
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(()-> new ResourceNotFoundException(id));
            flightList.add(flight);
        }
        flightList.sort(Comparator
                .comparing(Flight::getDeparture_time)
                .thenComparing(Flight::getDof));
        return flightList;
    }

    private List<Passenger> addPassengerList(List<Passenger> passengerList,
                                             List<Flight> flightList) {
        List<Passenger> passengers = new LinkedList<>();
        List<String> ticketID = generateTicketId(passengerList.size());
        for(Flight flight : flightList){
            for(String id : ticketID){
                for(Passenger psDTO : passengerList){
                    Passenger passenger = new Passenger();
                    passenger.setGreeting(psDTO.getGreeting());
                    passenger.setFirstName(psDTO.getFirstName());
                    passenger.setLastName(psDTO.getLastName());
                    passenger.setNational_id(psDTO.getNational_id());
                    passenger.setTicketId(Long.valueOf(id));
                    passenger.setFlight(flight);
                    passengerRepository.save(passenger);
                    passengers.add(passenger);
                }
            }
        }
        return passengers;
    }

    private User findUserByID(UUID id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("user id = "+id));
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
        return result.toString();
    }
    private String generatePNR() {
        return "TV" + generateRandomDigits(7);
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
