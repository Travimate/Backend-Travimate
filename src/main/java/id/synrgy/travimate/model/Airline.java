package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "airline")
public class Airline {

    @Id
    private String iata_code;

    private String airline_name;

    private String imageUrl;

//    @OneToMany(mappedBy = "airline")
//    private Set<Flight> flights;
}
