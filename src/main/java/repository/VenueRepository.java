package repository;

import model.Venue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VenueRepository extends BaseRepository<Venue> {

    public VenueRepository() {
        super(Venue.class, "SELECT e FROM Venue e", "name", "type");
    }
}
