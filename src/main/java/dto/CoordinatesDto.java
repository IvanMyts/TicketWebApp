package dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDto {
    @NotNull(message = "Координата X обязательна")
    @Min(value = -625, message = "Координата X должна быть больше -626")
    private Long x;

    @NotNull(message = "Координата Y обязательна")
    private Float y;
}
