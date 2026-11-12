package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @Valid
    @Embedded
    private Coordinates coordinates; //Поле не может быть null

    @Column(name = "creation_date", nullable = false, updatable = false)
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "person_id")
    private Person person; //Поле может быть null

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; //Поле может быть null

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double price; //Поле не может быть null, Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketType type; //Поле может быть null

    @NotNull
    @Positive
    @Max(100)
    @Column(nullable = false)
    private Integer discount; //Поле не может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 100

    @Positive
    @Column(name = "number")
    private Double number; //Поле может быть null, Значение поля должно быть больше 0

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue; //Поле может быть null

    @PrePersist
    void onCreate() {
        creationDate = ZonedDateTime.now();
    }
}