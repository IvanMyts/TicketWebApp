package model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long x; //Поле не может быть null

    @NotNull
    @Column(nullable = false)
    private Integer y; //Поле не может быть null

    @NotNull
    @Column(nullable = false)
    private Double z; //Поле не может быть null

    @NotBlank
    @Column(nullable = false)
    private String name; //Строка не может быть пустой, Поле не может быть null
}
