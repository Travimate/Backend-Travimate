package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.Flight;
import id.synrgy.travimate.model.FlightData;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class RouteDTO {
//    private UUID id;

    private String code;

    private Set<Airline> operated_airline;

    private Airport departure_airport;

    private Airport destination_airport;

    private Airport connecting_airport;

    private Set<Flight> flights;

    private Integer stops;

}
