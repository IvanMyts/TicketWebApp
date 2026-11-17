package repository;

import dto.request.PageQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRepository<T> {

    @PersistenceContext
    protected EntityManager em;

    private final String entity;
    private final String select;

    protected BaseRepository(String entity, String select) {
        this.entity = entity;
        this.select = select;
    }

    protected abstract String textColumn(String column);

    protected abstract T cast(Object row);

    public T findById(Object id) {
        Query query = em.createQuery("SELECT e FROM " + entity + " e WHERE e.id = :id");
        query.setParameter("id", id);
        List<?> found = query.getResultList();
        if (found.isEmpty()) {
            return null;
        }
        return cast(found.get(0));
    }

    public void save(T item) {
        em.persist(item);
    }

    public void delete(T item) {
        em.remove(item);
    }

    public List<T> findPage(PageQuery page) {
        Query query = em.createQuery(select + where(page) + orderBy(page));
        if (hasFilter(page)) {
            query.setParameter("filter", page.getFilterValue().toLowerCase());
        }
        query.setFirstResult(page.getPage() * page.getSize());
        query.setMaxResults(page.getSize());
        List<T> result = new ArrayList<>();
        for (Object row : query.getResultList()) {
            result.add(cast(row));
        }
        return result;
    }

    public long count(PageQuery page) {
        Query query = em.createQuery("SELECT COUNT(e) FROM " + entity + " e" + where(page));
        if (hasFilter(page)) {
            query.setParameter("filter", page.getFilterValue().toLowerCase());
        }
        return (Long) query.getSingleResult();
    }

    private boolean hasFilter(PageQuery page) {
        return page.getFilterBy() != null && page.getFilterValue() != null && !page.getFilterValue().isEmpty();
    }

    private String where(PageQuery page) {
        if (!hasFilter(page)) {
            return "";
        }
        return " WHERE LOCATE(:filter, LOWER(" + textColumn(page.getFilterBy()) + ")) > 0";
    }

    private String orderBy(PageQuery page) {
        String column = "e.id";
        if (page.getSortBy() != null && !page.getSortBy().equals("id")) {
            column = "LOWER(" + textColumn(page.getSortBy()) + ")";
        }
        String direction = " ASC";
        if (page.isDesc()) {
            direction = " DESC";
        }
        return " ORDER BY " + column + direction + ", e.id";
    }
}
