package dto.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {
    @QueryParam("page")
    @DefaultValue("0")
    @Min(value = 0, message = "Номер страницы не может быть отрицательным")
    private int page;

    @QueryParam("size")
    @DefaultValue("10")
    @Min(value = 1, message = "Размер страницы должен быть больше 0")
    @Max(value = 100, message = "Размер страницы не больше 100")
    private int size;

    @QueryParam("sortBy")
    @DefaultValue("id")
    private String sortBy;

    @QueryParam("desc")
    @DefaultValue("false")
    private boolean desc;

    @QueryParam("filterBy")
    private String filterBy;

    @QueryParam("filterValue")
    private String filterValue;
}
