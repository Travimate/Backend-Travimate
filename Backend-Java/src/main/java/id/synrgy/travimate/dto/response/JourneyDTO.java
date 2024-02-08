package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.BaseFare;
import id.synrgy.travimate.model.Flight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class JourneyDTO {

    private UUID flightDataId;

    private int stops;

    private Airport departure_airport;

    private Airport arrival_airport;

    private String airline_operator;

    private Flight.FlightClass flightClass;

    private BaseFare baseFare;

    private Set<Airline> airline;

    private List<RouteDTO> route;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dof;

    private LocalTime departure_time;

    private LocalTime arrival_time;

    private Long transit_time_minutes;

    private Long total_flight_time_minutes;

    private Integer seat_left;
}
