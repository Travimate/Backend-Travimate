package id.synrgy.travimate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
public class FlightRequest {
    private String dep;
    private String arr;
    private String airline;
    private int flightNumber;
    private String flightClass;
    private Date dof;
    private LocalTime depTime;
    private LocalTime arrTime;
    private Integer stock;

    // Constructors, getters, and setters
}
