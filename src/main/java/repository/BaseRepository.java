package repository;

import dto.PageQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Общий код репозиториев: CRUD и постраничный список с сортировкой и фильтром.
 * Наследник задаёт запрос выборки (с нужными JOIN FETCH) и строковые колонки.
 *
 * @param <T>  тип сущности
 * @param <ID> тип идентификатора
 */
public abstract class BaseRepository<T, ID> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> type;
    private final String alias;
    private final String select;

    /**
     * @param type   класс сущности
     * @param alias  псевдоним сущности в запросах, например "t"
     * @param select запрос выборки для страницы, например "SELECT t FROM Ticket t LEFT JOIN FETCH ..."
     */
    protected BaseRepository(Class<T> type, String alias, String select) {
        this.type = type;
        this.alias = alias;
        this.select = select;
    }

    /**
     * JPQL-выражение строковой колонки по её имени из API.
     * Для недопустимого имени бросает InvalidRequestException.
     */
    protected abstract String textColumn(String column);

    public List<T> findPage(PageQuery query) {
        TypedQuery<T> typedQuery = em.createQuery(select + where(query) + orderBy(query), type);
        setFilter(typedQuery, query);
        return typedQuery
                .setFirstResult(query.getPage() * query.getSize())
                .setMaxResults(query.getSize())
                .getResultList();
    }

    public long count(PageQuery query) {
        String jpql = "SELECT COUNT(" + alias + ") FROM " + type.getSimpleName() + " " + alias + where(query);
        TypedQuery<Long> typedQuery = em.createQuery(jpql, Long.class);
        setFilter(typedQuery, query);
        return typedQuery.getSingleResult();
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(type, id));
    }

    public void save(T entity) {
        em.persist(entity);
    }

    public void delete(T entity) {
        em.remove(entity);
    }

    public void flush() {
        em.flush();
    }

    private String where(PageQuery query) {
        if (!hasFilter(query)) {
            return "";
        }
        return " WHERE LOCATE(:filter, " + lowerText(query.getFilterBy()) + ") > 0";
    }

    private String orderBy(PageQuery query) {
        String sortBy = query.getSortBy();
        String column = sortBy == null || "id".equals(sortBy) ? alias + ".id" : lowerText(sortBy);
        return " ORDER BY " + column + (query.isDesc() ? " DESC" : " ASC") + ", " + alias + ".id ASC";
    }

    private void setFilter(TypedQuery<?> typedQuery, PageQuery query) {
        if (hasFilter(query)) {
            typedQuery.setParameter("filter", query.getFilterValue().toLowerCase(Locale.ROOT));
        }
    }

    private boolean hasFilter(PageQuery query) {
        return query.getFilterBy() != null && !query.getFilterBy().isEmpty()
                && query.getFilterValue() != null && !query.getFilterValue().isEmpty();
    }

    private String lowerText(String column) {
        return "LOWER(COALESCE(" + textColumn(column) + ", ''))";
    }
}
