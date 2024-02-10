package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "flight")
public class Flight {

    public enum FlightClass { ECONOMY, BUSINESS, PREMIUM_ECONOMY }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID flightID;

    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "route")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport dep;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arr;

    @ManyToOne
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @Enumerated(EnumType.STRING)
    private FlightClass flightClass;

    private LocalDate dof;

    private LocalTime departure_time;

    private LocalTime arrival_time;

    private LocalTime flight_time;

    private Integer stock;

    @ManyToMany(mappedBy = "flightList")
    private List<Orders> orders;

}
