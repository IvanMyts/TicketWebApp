package dto.response;

import lombok.*;
import model.Color;
import model.Country;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
    private Long id;
    private Color eyeColor;
    private Color hairColor;
    private LocationResponse location;
    private Double height;
    private Country nationality;
}
