package id.synrgy.travimate.dto.response;

import id.synrgy.travimate.model.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class OrderDTO {

    private UUID orderID;

    private String username;

    private String bookingID;

    private String bookedBy;

    private LocalDate bookedDate;

    private String bookedMail;

    private String pnrCode;

    private long amount;

    private List<FlightDTO> flightList;

    private List<PassengerDTO> passengerList;

    private Boolean completed;

    private boolean paid;

}
