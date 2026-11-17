package repository;

import model.Location;

import javax.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class LocationRepository extends BaseRepository<Location> {

    public LocationRepository() {
        super(Location.class, "SELECT e FROM Location e", Arrays.asList("name"), Arrays.asList("x", "y", "z"));
    }
}
