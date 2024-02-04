package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "flight_data")
public class FlightData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "departure_airport", nullable = false)
    private Airport departure;

    @ManyToOne
    @JoinColumn(name = "destination_airport", nullable = false)
    private Airport arrival;

    @ManyToOne
    @JoinColumn(name = "operated_airline", nullable = false)
    private Airline operated_airline;

    private Date flight_date;

    private Integer stops;

    private Boolean isDirect;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "base_fare")
    private BaseFare baseFare;

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_class")
    private Flight.FlightClass flightClass;

    @ManyToMany
    @JoinTable(
            name = "flightdata_route",
            joinColumns = @JoinColumn(name = "flight_data_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id"))
    private Set<Route> routeSet;

}
