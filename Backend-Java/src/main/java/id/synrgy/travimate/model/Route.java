package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "operated_airline", nullable = false)
    private Airline operated_airline;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departure_airport;

    @ManyToOne
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destination_airport;

    @ManyToOne
    @JoinColumn(name = "connecting_airport")
    private Airport connecting_airport;

    private Integer stops;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private Map<Integer, Flight> flights;

    @ManyToMany(mappedBy = "routeSet")
    private Set<FlightData> flightData;
}
