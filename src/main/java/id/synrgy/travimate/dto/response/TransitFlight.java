package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Flight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TransitFlight {

    private Flight firstFlight;
    private Flight secondFlight;
}
