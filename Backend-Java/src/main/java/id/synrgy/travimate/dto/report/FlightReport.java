package id.synrgy.travimate.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlightReport {
    private String airlineName;
    private String flightClass;
    private String imageUrl;
    private LocalTime depTime;
    private LocalTime arrTime;
    private String dep;
    private String arr;
    private String flightTime;
    private InputStream connector;
}
