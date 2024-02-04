package id.synrgy.travimate.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
public class FlightSearchDTO {

    private String departure;

    private String arrival;

    private Date dateOfFlight;

    private String dataInfo;

    private List<JourneyDTO> listOfFlight;
}
