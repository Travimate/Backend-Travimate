package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Orders extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private String bookingID;

    private String bookedBy;

    private LocalDate bookedDate;

    private String bookedMail;

    private String pnrCode;

    private long amount;

    @ManyToMany
    @JoinTable(
            name = "order_flight",
            joinColumns = @JoinColumn(name = "orders_id"),
            inverseJoinColumns = @JoinColumn(name = "flight_id")
    )
    private List<Flight> flightList;

    @OneToMany(mappedBy = "orders")
    private List<Passenger> passengerList;

    private Boolean completed;

    private Boolean paid;

    @ManyToOne
    @JoinColumn(name = "payment")
    private Payment payment;
}
