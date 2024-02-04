package id.synrgy.travimate.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
public class FlightRequestDTO {
    private String dep;
    private String arr;
    private String airline;
    private int flightNumber;
    private String flightClass;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dof;

    @JsonFormat(pattern = "HH.mm")
    private String depTime;

    @JsonFormat(pattern = "HH.mm")
    private String arrTime;

    private Integer stock;

    public LocalTime getDepTimeAsLocalTime() {
        return LocalTime.parse(depTime, DateTimeFormatter.ofPattern("HH.mm"));
    }

    public LocalTime getArrTimeAsLocalTime() {
        return LocalTime.parse(arrTime, DateTimeFormatter.ofPattern("HH.mm"));
    }
}
