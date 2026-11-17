package repository;

import exception.InvalidRequestException;
import model.Venue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VenueRepository extends BaseRepository<Venue> {

    public VenueRepository() {
        super("Venue", "SELECT e FROM Venue e");
    }

    @Override
    protected Venue cast(Object row) {
        return (Venue) row;
    }

    @Override
    protected String textColumn(String column) {
        switch (column) {
            case "name":
                return "e.name";
            case "type":
                return "CAST(e.type AS string)";
            default:
                throw new InvalidRequestException("Недопустимая колонка: " + column);
        }
    }
}
