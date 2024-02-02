package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class FlightDataDTO {
    private UUID id;

    private Airport departure;

    private Airport arrival;

    private Airline operated_airline;

    private Flight.FlightClass flightClass;

    private Integer stops;

    private Boolean isDirect;

    private BaseFare baseFare;

    private Set<RouteDTO> routeSet;
}
