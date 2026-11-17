package repository;

import exception.InvalidRequestException;
import model.Location;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationRepository extends BaseRepository<Location> {

    public LocationRepository() {
        super("Location", "SELECT e FROM Location e");
    }

    @Override
    protected Location cast(Object row) {
        return (Location) row;
    }

    @Override
    protected String textColumn(String column) {
        switch (column) {
            case "name":
                return "e.name";
            default:
                throw new InvalidRequestException("Недопустимая колонка: " + column);
        }
    }
}
