import { html, useState, useEffect, formatDate } from './lib.js';
import { api } from './api.js';
import { ENTITIES } from './entities.js';

const FIRST_PAGE = { page: 0, size: 10, sortBy: 'id', desc: false, filterBy: '', filterValue: '' };

export function EntityPage({ type, open, version }) {
    const cfg = ENTITIES[type];
    const textColumns = Object.entries(cfg.textColumns);
    const [query, setQuery] = useState(FIRST_PAGE);
    const [filter, setFilter] = useState({ by: textColumns[0][0], value: '' });
    const [data, setData] = useState(null);
    const [error, setError] = useState('');
    const [idToOpen, setIdToOpen] = useState('');

    useEffect(() => {
        api(type, { query })
            .then((result) => {
                if (result.content.length === 0 && query.page > 0) setQuery({ ...query, page: query.page - 1 });
                setData(result);
                setError('');
            })
            .catch((e) => setError(e.message));
    }, [type, query, version]);

    const update = (changes) => setQuery({ ...query, page: 0, ...changes });

    return html`
        <h1>${cfg.title}</h1>
        <div class="toolbar">
            <button onClick=${() => open('create', type)}>Создать</button>
            <form onSubmit=${(e) => { e.preventDefault(); open('view', type, idToOpen); }}>
                <input type="number" min="1" required placeholder="ID" value=${idToOpen}
                       onInput=${(e) => setIdToOpen(e.target.value)} />
                <button type="submit">Открыть по ID</button>
            </form>
        </div>
        <div class="toolbar">
            <form onSubmit=${(e) => { e.preventDefault(); update({ filterBy: filter.by, filterValue: filter.value }); }}>
                Фильтр:
                <select value=${filter.by} onChange=${(e) => setFilter({ ...filter, by: e.target.value })}>
                    ${textColumns.map(([key, title]) => html`<option key=${key} value=${key}>${title}</option>`)}
                </select>
                <input placeholder="содержит…" value=${filter.value} onInput=${(e) => setFilter({ ...filter, value: e.target.value })} />
                <button type="submit">Применить</button>
                <button type="button" onClick=${() => { setFilter({ ...filter, value: '' }); update({ filterBy: '', filterValue: '' }); }}>Сбросить</button>
            </form>
            <span>
                Сортировка:
                <select value=${query.sortBy} onChange=${(e) => update({ sortBy: e.target.value })}>
                    <option value="id">ID</option>
                    ${textColumns.map(([key, title]) => html`<option key=${key} value=${key}>${title}</option>`)}
                </select>
                <label><input type="checkbox" checked=${query.desc} onChange=${(e) => update({ desc: e.target.checked })} /> по убыванию</label>
            </span>
        </div>

        ${error && html`<p class="error">${error}</p>`}
        ${data && html`
            <div class="table-wrap">
                <table>
                    <thead><tr>${cfg.columns.map(([title]) => html`<th key=${title}>${title}</th>`)}<th></th></tr></thead>
                    <tbody>
                        ${data.content.length === 0 && html`<tr><td colspan=${cfg.columns.length + 1}>Нет записей</td></tr>`}
                        ${data.content.map((item) => html`
                            <tr key=${item.id}>
                                ${cfg.columns.map(([title, get, kind]) => html`<td key=${title}>${cell(get(item), kind, open)}</td>`)}
                                <td class="actions">
                                    <button onClick=${() => open('view', type, item.id)}>Просмотр</button>
                                    <button onClick=${() => open('edit', type, item.id)}>Изменить</button>
                                    <button onClick=${() => open('delete', type, item.id)}>Удалить</button>
                                </td>
                            </tr>`)}
                    </tbody>
                </table>
            </div>
            <div class="pager">
                <button disabled=${query.page === 0} onClick=${() => setQuery({ ...query, page: query.page - 1 })}>Назад</button>
                <span>Страница ${data.totalPages === 0 ? 0 : query.page + 1} из ${data.totalPages} (всего ${data.totalElements})</span>
                <button disabled=${query.page + 1 >= data.totalPages} onClick=${() => setQuery({ ...query, page: query.page + 1 })}>Вперёд</button>
                <select value=${query.size} onChange=${(e) => update({ size: Number(e.target.value) })}>
                    ${[5, 10, 20, 50].map((n) => html`<option key=${n} value=${n}>${n} на странице</option>`)}
                </select>
            </div>`}
    `;
}

function cell(value, kind, open) {
    if (value === null || value === undefined) return '—';
    if (kind === 'date') return formatDate(value);
    if (kind) {
        return html`<a href="#" onClick=${(e) => { e.preventDefault(); open('view', kind, value.id); }}>${ENTITIES[kind].label(value)}</a>`;
    }
    return String(value);
}
