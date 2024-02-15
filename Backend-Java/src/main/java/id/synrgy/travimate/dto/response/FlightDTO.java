package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.Flight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class FlightDTO {

    private UUID flightID;

    private String flightNumber;

    private Airport dep;

    private Airport arr;

    private Airline airline;

    private Flight.FlightClass flightClass;

    private LocalDate dof;

    private LocalTime departure_time;

    private LocalTime arrival_time;

    private LocalTime flight_time;

    private Integer stock;

}
