package repository;

import model.Venue;

import javax.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class VenueRepository extends BaseRepository<Venue> {

    public VenueRepository() {
        super(Venue.class, "SELECT e FROM Venue e", Arrays.asList("name", "type"), Arrays.asList("capacity"));
    }
}
