package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String bookingID;

    private String bookedBy;

    private Date bookedDate;

    private String bookedMail;

    private String pnrCode;

    @OneToMany(mappedBy = "order")
    private List<Flight> flightList;

    @OneToMany(mappedBy = "order")
    private List<Passenger> passengerList;

    private Boolean completed;

}
