package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment extends AuditModel{

    public enum PaymentMethod { EWALLET, BANK }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private double amount;

    @Enumerated(EnumType.STRING)
    private Payment.PaymentMethod method;

    private boolean confirmed;
}
