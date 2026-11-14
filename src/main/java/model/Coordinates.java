package model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Coordinates {
    @Min(-625)
    @Column(name = "coord_x", nullable = false)
    private long x; //Значение поля должно быть больше -626

    @Column(name = "coord_y", nullable = false)
    private float y;
}
