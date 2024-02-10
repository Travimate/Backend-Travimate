package id.synrgy.travimate.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class PassengerDTO {

    private UUID id;

    private String greeting;
    private String firstName;
    private String lastName;
    private int national_id;
    private String ticketId;
    private String flightNumber;
}
