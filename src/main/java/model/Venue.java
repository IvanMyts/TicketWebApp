package model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "venue")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    
    @NotBlank
    @Column(nullable = false, length = 100)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Positive
    private Integer capacity; //Поле может быть null, Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VenueType type; //Поле может быть null

}
