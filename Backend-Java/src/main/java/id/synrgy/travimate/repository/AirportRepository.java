package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {
    @Query("SELECT a FROM Airport a WHERE a.iata_code = %:name%")
    Optional<Airport> findByIataCode(@Param("name") String iataCode);
}
