package service;

import dto.request.VenueRequest;
import dto.response.VenueResponse;
import dto.request.PageQuery;
import dto.response.PageResponse;
import exception.ConflictException;
import exception.ObjectNotFoundException;
import mapper.VenueMapper;
import model.Venue;
import model.Ticket;
import repository.VenueRepository;
import repository.TicketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class VenueService {
    @Inject
    VenueRepository repository;
    @Inject
    TicketRepository ticketRepository;

    public PageResponse<VenueResponse> list(PageQuery query) {
        List<VenueResponse> content = new ArrayList<>();
        for (Venue item : repository.findPage(query)) {
            content.add(VenueMapper.toResponse(item));
        }
        return PageResponse.of(content, query.getSize(), repository.count(query));
    }

    public VenueResponse get(int id) {
        return VenueMapper.toResponse(findEntity(id));
    }

    public VenueResponse create(VenueRequest request) {
        Venue entity = VenueMapper.toEntity(request);
        repository.save(entity);
        return VenueMapper.toResponse(entity);
    }

    public VenueResponse update(int id, VenueRequest request) {
        Venue entity = findEntity(id);
        VenueMapper.update(entity, request);
        return VenueMapper.toResponse(entity);
    }

    public void delete(int id, Integer replacementId) {
        Venue entity = findEntity(id);
        List<Ticket> tickets = ticketRepository.findReferencing("venue", entity);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Площадка используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            Venue replacement = findEntity(replacementId);
            for (Ticket ticket : tickets) {
                ticket.setVenue(replacement);
            }
        }
        repository.delete(entity);
    }

    public Venue findEntity(int id) {
        Venue found = repository.findById(id);
        if (found == null) {
            throw new ObjectNotFoundException("Площадка с id " + id + " не найдена");
        }
        return found;
    }
}
