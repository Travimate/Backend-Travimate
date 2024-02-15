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

//    @Query(value = "SELECT DISTINCT o FROM Orders o " +
//            "INNER JOIN o.flightList f " +
//            "INNER JOIN f.airline a " +
//            "WHERE o.completed = true " +
//            "AND o.paid = true " +
//            "AND a.iataCode = :iataCode " +
//            "AND EXTRACT(YEAR FROM o.bookedDate) = :year")
//    List<Orders> findOrdersByAirlineAndYear(@Param("iataCode") String iataCode,
//                                            @Param("year") int year);
//
//    @Query(value = "SELECT DISTINCT o FROM Orders o " +
//            "INNER JOIN o.flightList f " +
//            "INNER JOIN f.airline a " +
//            "WHERE o.completed = true " +
//            "AND o.paid = true " +
//            "AND a.iataCode = :iataCode " +
//            "AND EXTRACT(YEAR FROM o.bookedDate) = :year " +
//            "AND EXTRACT(MONTH FROM o.bookedDate) = :month")
//    List<Orders> findOrdersByAirlineAndMonthAndYear(@Param("iataCode") String iataCode,
//                                                    @Param("month") int month,
//                                                    @Param("year") int year);
//
//    @Query(value = "SELECT DISTINCT o FROM Orders o " +
//            "INNER JOIN o.flightList f " +
//            "INNER JOIN f.airline a " +
//            "WHERE o.completed = true " +
//            "AND o.paid = true " +
//            "AND a.iataCode = :iataCode " +
//            "AND o.bookedDate BETWEEN :startDate " +
//            "AND :endDate")
//    List<Orders> findOrdersByAirlineAndPeriod(@Param("iataCode") String iataCode,
//                                              @Param("startDate") LocalDate startDate,
//                                              @Param("endDate") LocalDate endDate);


    @Query(value = "SELECT DISTINCT f FROM Orders o " +
        "INNER JOIN o.flightList f " +
        "INNER JOIN f.airline a " +
        "WHERE o.completed = true " +
            "AND o.paid = true " +
            "AND a.iata_code = :iataCode " +
            "AND EXTRACT(YEAR FROM o.bookedDate) = :year")
    List<Flight> findFlightsByAirlineAndYear(@Param("iataCode") String iataCode,
                                             @Param("year") int year);

    @Query(value = "SELECT DISTINCT f FROM Orders o " +
            "INNER JOIN o.flightList f " +
            "INNER JOIN f.airline a " +
            "WHERE o.completed = true " +
            "AND o.paid = true " +
            "AND a.iata_code = :iataCode " +
            "AND EXTRACT(YEAR FROM o.bookedDate) = :year " +
            "AND EXTRACT(MONTH FROM o.bookedDate) = :month")
    List<Flight> findFlightsByAirlineAndMonthAndYear(@Param("iataCode") String iataCode,
                                                     @Param("month") int month,
                                                     @Param("year") int year);

    @Query(value = "SELECT DISTINCT f FROM Orders o " +
            "INNER JOIN o.flightList f " +
            "INNER JOIN f.airline a " +
            "WHERE o.completed = true " +
            "AND o.paid = true " +
            "AND a.iata_code = :iataCode " +
            "AND o.bookedDate BETWEEN :startDate " +
            "AND :endDate")
    List<Flight> findFlightsByAirlineAndPeriod(@Param("iataCode") String iataCode,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

}