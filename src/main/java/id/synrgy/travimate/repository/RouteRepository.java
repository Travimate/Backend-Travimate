package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    Optional<Route> findById(String id);

    @Query("SELECT r FROM Route r WHERE r.code = :code " +
            "AND r.operated_airline = :airline " +
            "AND r.connecting_airport IS NULL")
    Optional<Route> findDirectRouteAndOperatedAirline(@Param("code") String code,
                                                      @Param("airline") Airline operatedAirline);
    @Query("SELECT r FROM Route r WHERE r.code = :code " +
            "AND r.connecting_airport = :connecting_airport " +
            "AND r.operated_airline = :airline")
    Optional<Route> findByRouteCodeAndOperatedAirline(@Param("code") String code,
                                                  @Param("connecting_airport")Airport connectingAirport,
                                                  @Param("airline") Airline operatedAirline);
    @Query("SELECT r FROM Route r JOIN r.flightData fd WHERE r.code = :code AND r.operated_airline = :airline")
    Route findByCodeAndOperatedAirline2(@Param("code") String code, @Param("airline") Airline operatedAirline);
}
