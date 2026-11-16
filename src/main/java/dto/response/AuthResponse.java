package dto.response;

import lombok.*;

/** Текущий пользователь. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String login;
}
