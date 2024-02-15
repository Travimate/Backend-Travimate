package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.Flight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class RouteProcess {

    private String code;

    private Set<Airline> operated_airline;

    private Airport departure_airport;

    private Airport destination_airport;

    private Airport connecting_airport;

    private Set<Flight> flights;

    private Integer stops;

}
