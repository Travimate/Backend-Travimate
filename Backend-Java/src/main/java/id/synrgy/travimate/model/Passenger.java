package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "passanger")
public class Passenger {

    public enum PassengerType { ADULT, CHILD }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String greeting;
    private String firstName;
    private String lastName;
    private int national_id;
    private String ticketId;

    @Enumerated(EnumType.STRING)
    private Passenger.PassengerType type;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orders;
}
