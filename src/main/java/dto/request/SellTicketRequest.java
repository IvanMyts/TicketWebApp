package dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellTicketRequest {
    @NotNull(message = "Укажите покупателя")
    private Long personId;

    @NotNull(message = "Укажите сумму продажи")
    @Positive(message = "Сумма продажи должна быть больше 0")
    private Double price;
}
