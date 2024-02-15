package id.synrgy.travimate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class OrderRequestDTO {

    private String bookedBy;
    private String bookedMail;
    private UUID flightDataID;
    private List<UUID> flightID;
    private List<PassengerRequestDTO> passengerList;
}
