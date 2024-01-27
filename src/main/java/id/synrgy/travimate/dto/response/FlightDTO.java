package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.Flight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class FlightDTO {

    private UUID id;

    private String flightNumber;

    private Airport dep;

    private Airport arr;

    private Airline airline;

    private Flight.FlightClass flightClass;

    private Date dof;

    private LocalTime departure_time;

    private LocalTime arrival_time;

    private LocalTime flight_time;

    private Integer stock;

    private Long price;
}
