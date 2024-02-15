package id.synrgy.travimate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class AirlineSalesReportDTO {

    private String periode;
    private Integer month;
    private Integer year;
    private LocalDate week;
    private LocalDate startDate;
    private LocalDate endDate;
}
