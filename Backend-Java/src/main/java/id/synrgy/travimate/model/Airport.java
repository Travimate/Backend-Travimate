package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "airport")
public class Airport {

    @Id
    private String iata_code;

    private String airport_name;

    private String city;

    private String country;

}
