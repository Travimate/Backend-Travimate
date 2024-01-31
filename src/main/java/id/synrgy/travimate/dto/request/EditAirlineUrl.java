package id.synrgy.travimate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EditAirlineUrl {

    private String iataCode;

    private String url;
}
