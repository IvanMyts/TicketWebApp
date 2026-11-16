package dto.response;

import lombok.*;

/** Ошибка для клиента: текст, который можно показать пользователю. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
}
