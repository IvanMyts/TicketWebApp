package repository;

import dto.request.PageQuery;
import exception.InvalidRequestException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

public abstract class BaseRepository<T> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> type;
    private final String select;
    private final List<String> textColumns;
    private final List<String> sortColumns;

    protected BaseRepository(Class<T> type, String select, List<String> textColumns, List<String> sortColumns) {
        this.type = type;
        this.select = select;
        this.textColumns = textColumns;
        this.sortColumns = sortColumns;
    }

    public T findById(Object id) {
        return em.find(type, id);
    }

    public List<T> findReferencing(String relation, Object value) {
        String jpql = "SELECT e FROM " + type.getSimpleName() + " e WHERE e." + relation + " = :value";
        return em.createQuery(jpql, type).setParameter("value", value).getResultList();
    }

    public void save(T item) {
        em.persist(item);
    }

    public void delete(T item) {
        em.remove(item);
    }

    public List<T> findPage(PageQuery page) {
        TypedQuery<T> query = em.createQuery(select + where(page) + orderBy(page), type);
        setFilter(query, page);
        query.setFirstResult(page.getPage() * page.getSize());
        query.setMaxResults(page.getSize());
        return query.getResultList();
    }

    public long count(PageQuery page) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM " + type.getSimpleName() + " e" + where(page), Long.class);
        setFilter(query, page);
        return query.getSingleResult();
    }

    private boolean hasFilter(PageQuery page) {
        return page.getFilterBy() != null && page.getFilterValue() != null && !page.getFilterValue().isEmpty();
    }

    private void setFilter(Query query, PageQuery page) {
        if (hasFilter(page)) {
            query.setParameter("filter", page.getFilterValue().toLowerCase());
        }
    }

    private String where(PageQuery page) {
        if (!hasFilter(page)) {
            return "";
        }
        return " WHERE LOCATE(:filter, " + textColumn(page.getFilterBy()) + ") > 0";
    }

    private String orderBy(PageQuery page) {
        String column = sortColumn(page.getSortBy());
        String direction = " ASC";
        if (page.isDesc()) {
            direction = " DESC";
        }
        return " ORDER BY " + column + direction + ", e.id";
    }

    private String sortColumn(String column) {
        if (column == null || column.equals("id")) {
            return "e.id";
        }
        if (sortColumns.contains(column)) {
            return "e." + column;
        }
        return textColumn(column);
    }

    private String textColumn(String column) {
        if (!textColumns.contains(column)) {
            throw new InvalidRequestException("Недопустимая колонка: " + column);
        }
        return "LOWER(CAST(e." + column + " AS string))";
    }
}
