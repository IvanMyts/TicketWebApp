package dto.response;

import lombok.*;

import java.util.List;

/** Страница таблицы: строки и данные для переключателя страниц. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;

    public static <T> PageResponse<T> of(List<T> content, int size, long totalElements) {
        return new PageResponse<>(content, totalElements, (int) ((totalElements + size - 1) / size));
    }
}
