package dto;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Positive;
import lombok.*;
import model.Color;
import model.Country;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequest {
    private Color eyeColor;
    private Color hairColor;

    /** ID существующей локации. Взаимоисключающе с {@link #location}. */
    private Long locationId;

    /** Новая локация, создаётся вместе с человеком. Взаимоисключающе с {@link #locationId}. */
    @Valid
    private LocationRequest location;

    @Positive(message = "Рост должен быть больше 0")
    private Double height;

    private Country nationality;

    @JsonbTransient
    @AssertTrue(message = "Укажите либо существующую локацию, либо новую, но не обе сразу")
    public boolean isLocationUnambiguous() {
        return locationId == null || location == null;
    }
}
