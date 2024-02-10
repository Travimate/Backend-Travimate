package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    public enum Roles { admin, user }

    @Id
    private Integer id;
    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private List<Roles> roles;

}
