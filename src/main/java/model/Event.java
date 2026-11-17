package model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDateTime date; //Поле не может быть null

    @Column(name = "min_age", nullable = false)
    private long minAge;
}
