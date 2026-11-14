package service;

import dto.VenueRequest;
import dto.VenueResponse;
import dto.PageQuery;
import dto.PageResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
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

    public PageResponse<VenueResponse> list(PageQuery query) {
        List<VenueResponse> content = repository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getPage(), query.getSize(), repository.count(query));
    }

    public VenueResponse get(int id) {
        return mapper.toResponse(findEntity(id));
    }

    public VenueResponse create(VenueRequest request) {
        return mapper.toResponse(createEntity(request));
    }

    public VenueResponse update(int id, VenueRequest request) {
        Venue entity = findEntity(id);
        mapper.update(entity, request);
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
            if (replacementId == id) {
                throw new InvalidRequestException("Замена должна отличаться от удаляемой площадки");
            }
            Venue replacement = findEntity(replacementId);
            tickets.forEach(ticket -> ticket.setVenue(replacement));
        }
        repository.delete(entity);
    }

    public Venue findEntity(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Площадка с id " + id + " не найдена"));
    }

    /** Связь при создании/изменении билета: существующий объект по id, новый из запроса или null. */
    public Venue resolve(Integer id, VenueRequest request) {
        if (id != null) {
            return findEntity(id);
        }
        return request != null ? createEntity(request) : null;
    }

    private Venue createEntity(VenueRequest request) {
        Venue entity = mapper.toEntity(request);
        repository.save(entity);
        return entity;
    }
}
