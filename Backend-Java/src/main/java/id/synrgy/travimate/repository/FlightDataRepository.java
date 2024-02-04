package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.FlightData;
import id.synrgy.travimate.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FlightDataRepository extends JpaRepository<FlightData, UUID> {
//    Set<FlightData> findByDepartureAndArrival(Airport dep, Airport arr);

    @Query("SELECT fd FROM FlightData fd " +
            "JOIN fd.departure dep " +
            "JOIN fd.arrival arr " +
            "WHERE dep = :departureAirport " +
            "AND arr = :arrivalAirport " +
            "AND fd.flight_date = :flightDate")
    List<FlightData> findFlightDataByDepartureArrivalAndDate(
            @Param("departureAirport") Airport departureAirport,
            @Param("arrivalAirport") Airport arrivalAirport,
            @Param("flightDate") Date flightDate
    );

    @Query("SELECT r FROM Route r JOIN r.flightData fd WHERE r.code = :code AND r.operated_airline = :airline")
    Set<Route> findByCodeAndOperatedAirline(@Param("code") String code, @Param("airline") Airline operatedAirline);

    @Query("SELECT fd FROM FlightData fd " +
            "JOIN fd.departure dep " +
            "JOIN fd.arrival arr " +
            "WHERE dep = :departureAirport " +
            "AND arr = :arrivalAirport " +
            "AND fd.isDirect = true " +
            "AND fd.flight_date = :flightDate")
    List<FlightData> findDirectFlightsByDepartureAndArrival(
            @Param("departureAirport") Airport departureAirport,
            @Param("arrivalAirport") Airport arrivalAirport,
            @Param("flightDate") Date flightDate
    );

}
