package dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    @NotBlank(message = "Название мероприятия не может быть пустым")
    @Size(max = 100, message = "Название мероприятия не длиннее 100 символов")
    private String name;

    @NotNull(message = "Дата мероприятия обязательна")
    private LocalDateTime date;

    @NotNull(message = "Минимальный возраст обязателен")
    private Long minAge;
}
