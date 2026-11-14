package repository;

import exception.InvalidRequestException;
import model.Venue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VenueRepository extends BaseRepository<Venue, Integer> {

    public VenueRepository() {
        super(Venue.class, "v", "SELECT v FROM Venue v");
    }

    @Override
    protected String textColumn(String column) {
        return switch (column) {
            case "name" -> "v.name";
            case "type" -> "CAST(v.type AS string)";
            default -> throw new InvalidRequestException("Недопустимая колонка: " + column);
        };
    }
}
