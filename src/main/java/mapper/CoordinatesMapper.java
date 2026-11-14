package mapper;

import dto.CoordinatesDto;
import model.Coordinates;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoordinatesMapper {

    public Coordinates toEntity(CoordinatesDto dto) {
        if (dto == null) {
            return null;
        }
        return new Coordinates(dto.getX(), dto.getY());
    }

    public CoordinatesDto toDto(Coordinates entity) {
        if (entity == null) {
            return null;
        }
        return new CoordinatesDto(entity.getX(), entity.getY());
    }
}
