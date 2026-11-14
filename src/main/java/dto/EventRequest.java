package dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    @NotBlank(message = "Название мероприятия не может быть пустым")
    private String name;

    @NotNull(message = "Дата мероприятия обязательна")
    private LocalDateTime date;

    @NotNull(message = "Минимальный возраст обязателен")
    private Long minAge;
}
