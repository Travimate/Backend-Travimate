package id.synrgy.travimate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class FlightDataRequestDTO {
    private String airline;
    private String dep;
    private String arr;
    private Integer stops;
    private Long adultFare;
    private Long childFare;
    private Boolean sameAsAdult;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String flightClass;
    private String connectingAirport;
    private Boolean isDirect;
}