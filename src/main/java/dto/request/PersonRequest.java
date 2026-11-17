package dto.request;

import lombok.*;
import model.Color;
import model.Country;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequest {
    private Color eyeColor;
    private Color hairColor;

    private Long locationId;

    @Positive(message = "Рост должен быть больше 0")
    private Double height;

    private Country nationality;
}
