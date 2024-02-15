package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Flight;
import id.synrgy.travimate.model.Orders;
import id.synrgy.travimate.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {

    List<Orders> findByUsers(Users users);

    @Query(value = "SELECT DISTINCT f FROM Orders o " +
        "INNER JOIN o.flightList f " +
        "INNER JOIN f.airline a " +
        "WHERE o.completed = true " +
            "AND o.paid = true " +
            "AND a.iata_code = :iataCode " +
            "AND EXTRACT(YEAR FROM o.bookedDate) = :year")
    List<Flight> findFlightsByAirlineAndYear(@Param("iataCode") String iataCode,
                                             @Param("year") int year);

    @Query(value = "SELECT DISTINCT f.* FROM orders o " +
            "INNER JOIN order_flight ofl ON o.orderid = ofl.orders_id " +
            "INNER JOIN flight f ON ofl.flight_id = f.flightid " +
            "WHERE o.completed = true " +
            "AND o.paid = true " +
            "AND f.airline_id = :iataCode " +
            "AND EXTRACT(YEAR FROM o.booked_date) = :year " +
            "AND EXTRACT(MONTH FROM o.booked_date) = :month", nativeQuery = true)
    List<Flight> findFlightsByAirlineAndMonthAndYear(@Param("iataCode") String iataCode,
                                                     @Param("month") int month,
                                                     @Param("year") int year);

    @Query(value = "SELECT DISTINCT f.* FROM orders o " +
            "INNER JOIN order_flight ofl ON o.orderid = ofl.orders_id " +
            "INNER JOIN flight f ON ofl.flight_id = f.flightid " +
            "WHERE o.completed = true " +
            "AND o.paid = true " +
            "AND f.airline_id = :iataCode " +
            "AND o.booked_date BETWEEN :startDate " +
            "AND :endDate", nativeQuery = true)
    List<Flight> findFlightsByAirlineAndPeriod(@Param("iataCode") String iataCode,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

}