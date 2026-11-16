package service;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
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
import java.util.List;

@ApplicationScoped
@Transactional
public class VenueService {
    @Inject
    VenueRepository repository;
    @Inject
    TicketRepository ticketRepository;
    @Inject
    VenueMapper mapper;
    @Inject
    javax.enterprise.event.Event<EntityChangeEvent> changes;

    public PageResponse<VenueResponse> list(PageQuery query) {
        List<VenueResponse> content = repository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getSize(), repository.count(query));
    }

    public VenueResponse get(int id) {
        return mapper.toResponse(findEntity(id));
    }

    public VenueResponse create(VenueRequest request) {
        Venue entity = mapper.toEntity(request);
        repository.save(entity);
        changed(entity.getId());
        return mapper.toResponse(entity);
    }

    public VenueResponse update(int id, VenueRequest request) {
        Venue entity = findEntity(id);
        mapper.update(entity, request);
        changed(id);
        return mapper.toResponse(entity);
    }

    /** Билеты с этим объектом перепривязываются к replacementId, затем объект удаляется. */
    public void delete(int id, Integer replacementId) {
        Venue entity = findEntity(id);
        List<Ticket> tickets = ticketRepository.findByVenue(entity);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Площадка используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            Venue replacement = findEntity(replacementId);
            tickets.forEach(ticket -> ticket.setVenue(replacement));
        }
        repository.delete(entity);
        changed(id);
    }

    public Venue findEntity(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Площадка с id " + id + " не найдена"));
    }

    private void changed(long id) {
        changes.fire(new EntityChangeEvent(EntityType.VENUE, id));
    }
}
