package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {
    @Query(value = "SELECT * FROM public.flight f " +
            "WHERE f.departure_airport_id = UPPER(:dep) " +
            "AND f.arrival_airport_id = UPPER(:arr) " +
            "AND f.dof = :dof " +
            "AND f.airline_id = UPPER(:airline) " +
            "AND f.flight_class = UPPER(:class)", nativeQuery = true)
    List<Flight> findByAirportAndAirline(@Param("dep") String dep,
                                         @Param("arr") String arr,
                                         @Param("dof") Date dof,
                                         @Param("airline") String airline,
                                         @Param("class") String flightClass);
}
