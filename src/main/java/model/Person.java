package model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", length = 20)
    private Color eyeColor; //Поле может быть null

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", length = 20)
    private Color hairColor; //Поле может быть null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location; //Поле может быть null

    @Positive
    private Double height; //Поле может быть null, Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Country nationality; //Поле может быть null
}
