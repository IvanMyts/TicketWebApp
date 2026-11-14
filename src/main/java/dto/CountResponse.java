package dto;

import lombok.*;

/** Ответ на «количество объектов, у которых number меньше заданного». */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountResponse {
    private long count;
}
