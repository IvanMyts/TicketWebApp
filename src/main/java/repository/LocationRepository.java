package repository;

import exception.InvalidRequestException;
import model.Location;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationRepository extends BaseRepository<Location, Long> {

    public LocationRepository() {
        super(Location.class, "l", "SELECT l FROM Location l");
    }

    @Override
    protected String textColumn(String column) {
        return switch (column) {
            case "name" -> "l.name";
            default -> throw new InvalidRequestException("Недопустимая колонка: " + column);
        };
    }
}
