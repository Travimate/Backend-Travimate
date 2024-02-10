package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.FlightData;
import id.synrgy.travimate.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.*;

public interface FlightDataRepository extends JpaRepository<FlightData, UUID> {

    @Query("SELECT fd FROM FlightData fd " +
            "JOIN fd.departure dep " +
            "JOIN fd.arrival arr " +
            "WHERE dep = :departureAirport " +
            "AND arr = :arrivalAirport " +
            "AND fd.flight_date = :flightDate")
    List<FlightData> findFlightDataByDepartureArrivalAndDate(
            @Param("departureAirport") Airport departureAirport,
            @Param("arrivalAirport") Airport arrivalAirport,
            @Param("flightDate") LocalDate flightDate
    );

    @Query("SELECT fd FROM FlightData fd " +
            "JOIN fd.departure dep " +
            "JOIN fd.arrival arr " +
            "JOIN fd.operated_airline ope " +
            "WHERE dep = :departureAirport " +
            "AND arr = :arrivalAirport " +
            "AND ope = :operatedAirline " +
            "AND fd.isDirect = true " +
            "AND fd.flight_date = :flightDate")
    Optional<FlightData> findDirectFlightsByDepartureAndArrival(
            @Param("departureAirport") Airport departureAirport,
            @Param("arrivalAirport") Airport arrivalAirport,
            @Param("operatedAirline") Airline operatedAirline,
            @Param("flightDate") LocalDate flightDate
    );

    @Query("SELECT fd FROM FlightData fd " +
            "JOIN fd.departure dep " +
            "JOIN fd.arrival arr " +
            "JOIN fd.connecting con " +
            "JOIN fd.operated_airline ope " +
            "WHERE dep = :departureAirport " +
            "AND arr = :arrivalAirport " +
            "AND con = :connectingAirport " +
            "AND ope = :operatedAirline " +
            "AND fd.flight_date = :flightDate")
    Optional<FlightData> findFlightsByDepartureAndArrivalAndConnecting(
            @Param("departureAirport") Airport departureAirport,
            @Param("arrivalAirport") Airport arrivalAirport,
            @Param("connectingAirport") Airport connectingAirport,
            @Param("operatedAirline") Airline operatedAirline,
            @Param("flightDate") LocalDate flightDate
    );

}
