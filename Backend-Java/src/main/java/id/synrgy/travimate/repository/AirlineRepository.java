package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, String> {
    @Query("SELECT a FROM Airline a WHERE a.iata_code = %:name%")
    Optional<Airline> findByIataCode(@Param("name") String iataCode);
}