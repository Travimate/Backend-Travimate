package id.synrgy.travimate.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlightReport {
    private String airlineName;
    private String flightClass;
    private String imageUrl;
}
