package service;

import dto.request.PageQuery;
import dto.request.VenueRequest;
import dto.response.PageResponse;
import dto.response.VenueResponse;
import exception.ConflictException;
import exception.ObjectNotFoundException;
import mapper.VenueMapper;
import model.Ticket;
import model.Venue;
import repository.TicketRepository;
import repository.VenueRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class VenueService {
    @Inject
    VenueRepository venueRepository;
    @Inject
    TicketRepository ticketRepository;

    public PageResponse<VenueResponse> list(PageQuery query) {
        List<VenueResponse> content = new ArrayList<>();
        for (Venue item : venueRepository.findPage(query)) {
            content.add(VenueMapper.toResponse(item));
        }
        return PageResponse.of(content, query.getSize(), venueRepository.count(query));
    }

    public VenueResponse get(int id) {
        return VenueMapper.toResponse(findEntity(id));
    }

    public VenueResponse create(VenueRequest request) {
        Venue venue = VenueMapper.toEntity(request);
        venueRepository.save(venue);
        return VenueMapper.toResponse(venue);
    }

    public VenueResponse update(int id, VenueRequest request) {
        Venue venue = findEntity(id);
        VenueMapper.update(venue, request);
        return VenueMapper.toResponse(venue);
    }

    public void delete(int id, Integer replacementId) {
        Venue venue = findEntity(id);
        List<Ticket> tickets = ticketRepository.findReferencing("venue", venue);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Площадка используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            Venue replacement = findEntity(replacementId);
            for (Ticket ticket : tickets) {
                ticket.setVenue(replacement);
            }
        }
        venueRepository.delete(venue);
    }

    public Venue findEntity(int id) {
        Venue found = venueRepository.findById(id);
        if (found == null) {
            throw new ObjectNotFoundException("Площадка с id " + id + " не найдена");
        }
        return found;
    }
}
