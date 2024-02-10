package id.synrgy.travimate.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerReport {

    private Integer no;
    private String firstName;
    private String lastName;
    private String type;
    private String route;
    private String ticket;
    private String baggage;
}
