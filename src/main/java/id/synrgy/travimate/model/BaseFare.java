package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "base_fare")
public class BaseFare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private long childBaseFare;

    private long adultBaseFare;
}
