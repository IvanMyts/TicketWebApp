package dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Укажите логин")
    @Size(max = 255, message = "Логин не длиннее 255 символов")
    private String login;

    @NotBlank(message = "Укажите пароль")
    @Size(max = 72, message = "Пароль не длиннее 72 символов")
    private String password;
}
