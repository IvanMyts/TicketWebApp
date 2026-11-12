package model;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String login;

    @NotNull
    @Column(name = hash_password, nullable = false)
    private String hashPassword;
}
