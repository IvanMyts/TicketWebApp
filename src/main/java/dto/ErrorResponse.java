package dto;

import lombok.*;

import java.util.List;

/** Единый формат ошибки для клиента: общее сообщение и ошибки по полям. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private List<FieldError> fieldErrors;

    public ErrorResponse(String message) {
        this(message, List.of());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
