package id.synrgy.travimate.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AirlineRevenueReport {
    private String flightNumber;
    private String orderTime;
    private String stringTotalOrder;
    private String stringTotalAmount;
    private Integer totalOrder;
    private Integer totalAmount;

}
