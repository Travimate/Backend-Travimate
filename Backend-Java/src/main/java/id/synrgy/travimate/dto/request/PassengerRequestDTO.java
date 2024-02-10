package id.synrgy.travimate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PassengerRequestDTO {

    private String greeting;
    private String firstName;
    private String lastName;
    private int national_id;
    private String type;
}
