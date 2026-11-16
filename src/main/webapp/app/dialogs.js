import { html, useState, useEffect, formatDate } from './lib.js';
import { api } from './api.js';
import { ENTITIES, ENUMS, LABELS } from './entities.js';

export function Modal({ title, onClose, children }) {
    return html`
        <div class="overlay" onClick=${onClose}>
            <div class="modal" onClick=${(e) => e.stopPropagation()}>
                <header><h2>${title}</h2><button onClick=${onClose}>✕</button></header>
                ${children}
            </div>
        </div>`;
}

/** Список существующих объектов для выбора связи (первые 100). */
export function RefSelect({ type, value, onChange, exclude, required }) {
    const [items, setItems] = useState([]);
    useEffect(() => {
        api(type, { query: { size: 100 } }).then((page) => setItems(page.content)).catch(() => setItems([]));
    }, [type]);
    return html`
        <select value=${value} required=${required} onChange=${(e) => onChange(e.target.value)}>
            <option value="">— не выбрано —</option>
            ${items
                .filter((item) => String(item.id) !== String(exclude))
                .map((item) => html`<option key=${item.id} value=${String(item.id)}>${ENTITIES[type].label(item)}</option>`)}
        </select>`;
}

// ---------- Просмотр ----------

/** Окно просмотра: все поля объекта и полная информация о связанных объектах. */
export function ViewDialog({ type, id, open, onClose, version }) {
    const cfg = ENTITIES[type];
    const [item, setItem] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        api(`${type}/${id}`)
            .then((result) => { setItem(result); setError(''); })
            .catch((e) => { setItem(null); setError(e.message); });
    }, [type, id, version]);

    return html`
        <${Modal} title=${`Просмотр: ${cfg.one} #${id}`} onClose=${onClose}>
            ${error && html`<p class="error">${error}</p>`}
            ${item && html`
                <${Details} data=${item} />
                <div class="buttons">
                    <button onClick=${() => open('edit', type, id)}>Изменить</button>
                    <button onClick=${() => open('delete', type, id)}>Удалить</button>
                </div>`}
        <//>`;
}

function Details({ data }) {
    return html`
        <table class="details"><tbody>
            ${Object.entries(data).map(([key, value]) => html`
                <tr key=${key}>
                    <th>${LABELS[key] || key}</th>
                    <td>${value === null || value === undefined ? '—'
                        : typeof value === 'object' ? html`<${Details} data=${value} />`
                        : key === 'creationDate' || key === 'date' ? formatDate(value)
                        : String(value)}</td>
                </tr>`)}
        </tbody></table>`;
}

// ---------- Создание и изменение ----------

/** Окно создания (id не задан) или изменения объекта. */
export function FormDialog({ type, id, open, onClose }) {
    const cfg = ENTITIES[type];
    const editing = id !== undefined;
    const [values, setValues] = useState(editing ? null : emptyValues(cfg));
    const [error, setError] = useState('');

    useEffect(() => {
        if (editing) {
            api(`${type}/${id}`).then((item) => setValues(toValues(cfg, item))).catch((e) => setError(e.message));
        }
    }, []);

    const submit = async (e) => {
        e.preventDefault();
        const clientError = validate(cfg, values);
        if (clientError) return setError(clientError);
        try {
            const saved = await api(editing ? `${type}/${id}` : type, {
                method: editing ? 'PUT' : 'POST',
                body: toRequest(cfg, values),
            });
            open('view', type, saved.id);
        } catch (err) {
            setError(err.message);
        }
    };

    return html`
        <${Modal} title=${editing ? `Изменение: ${cfg.one} #${id}` : `Создание: ${cfg.one}`} onClose=${onClose}>
            ${values && html`
                <form class="form" onSubmit=${submit}>
                    ${cfg.fields.map((field) => html`
                        <label key=${field.name}>
                            ${field.label}${field.required ? ' *' : ''}
                            <${Input} field=${field} value=${values[field.name]}
                                      onChange=${(value) => setValues({ ...values, [field.name]: value })} />
                        </label>`)}
                    ${error && html`<p class="error">${error}</p>`}
                    <div class="buttons">
                        <button type="submit">Сохранить</button>
                        <button type="button" onClick=${onClose}>Отмена</button>
                    </div>
                </form>`}
            ${!values && error && html`<p class="error">${error}</p>`}
        <//>`;
}

/** Поле формы. Атрибуты required/min/max/maxLength не дают ввести недопустимое значение. */
function Input({ field, value, onChange }) {
    const change = (e) => onChange(e.target.value);
    if (field.ref) {
        return html`<${RefSelect} type=${field.ref} value=${value} onChange=${onChange} />`;
    }
    if (field.enum) {
        return html`
            <select value=${value} required=${field.required} onChange=${change}>
                <option value="">—</option>
                ${ENUMS[field.enum].map((option) => html`<option key=${option} value=${option}>${option}</option>`)}
            </select>`;
    }
    if (field.type === 'datetime') {
        return html`<input type="datetime-local" value=${value} required=${field.required} onInput=${change} />`;
    }
    if (field.type === 'int' || field.type === 'number') {
        return html`<input type="number" step=${field.type === 'int' ? 1 : 'any'} min=${field.min} max=${field.max}
                           value=${value} required=${field.required} onInput=${change} />`;
    }
    return html`<input value=${value} required=${field.required} maxLength=${field.maxLength} onInput=${change} />`;
}

function emptyValues(cfg) {
    return Object.fromEntries(cfg.fields.map((field) => [field.name, '']));
}

/** Ответ сервера → значения полей формы (строки). */
function toValues(cfg, item) {
    return Object.fromEntries(cfg.fields.map((field) => {
        let value = field.ref ? item[field.from]?.id : field.name.split('.').reduce((obj, key) => obj?.[key], item);
        if (value === null || value === undefined) return [field.name, ''];
        if (field.type === 'datetime') value = String(value).slice(0, 16);
        return [field.name, String(value)];
    }));
}

/** Значения формы → тело запроса. "coordinates.x" превращается во вложенный объект. */
function toRequest(cfg, values) {
    const request = {};
    for (const field of cfg.fields) {
        const raw = values[field.name];
        const numeric = field.ref || field.type === 'int' || field.type === 'number';
        const value = raw === '' ? null : numeric ? Number(raw) : raw;
        const path = field.name.split('.');
        let target = request;
        path.slice(0, -1).forEach((key) => { target = target[key] = target[key] || {}; });
        target[path[path.length - 1]] = value;
    }
    return request;
}

/** Проверка «больше 0», которую нельзя задать атрибутом min. */
function validate(cfg, values) {
    const field = cfg.fields.find((f) => f.positive && values[f.name] !== '' && Number(values[f.name]) <= 0);
    return field ? `Поле «${field.label}» должно быть больше 0` : '';
}

// ---------- Удаление ----------

/** Окно удаления. Для вспомогательных объектов можно выбрать замену, к которой перейдут ссылки. */
export function DeleteDialog({ type, id, onClose }) {
    const cfg = ENTITIES[type];
    const [item, setItem] = useState(null);
    const [replacementId, setReplacementId] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        api(`${type}/${id}`).then(setItem).catch((e) => setError(e.message));
    }, []);

    const remove = async () => {
        try {
            await api(`${type}/${id}`, { method: 'DELETE', query: { replacementId } });
            onClose();
        } catch (e) {
            setError(e.message);
        }
    };

    return html`
        <${Modal} title=${`Удаление: ${cfg.one} #${id}`} onClose=${onClose}>
            ${item && html`
                <p>Удалить «${cfg.label(item)}»?</p>
                ${type !== 'tickets' && html`
                    <label class="form">
                        Если на объект ссылаются другие, выберите замену — ссылки перейдут на неё:
                        <${RefSelect} type=${type} value=${replacementId} onChange=${setReplacementId} exclude=${id} />
                    </label>`}
                <div class="buttons">
                    <button class="danger" onClick=${remove}>Удалить</button>
                    <button onClick=${onClose}>Отмена</button>
                </div>`}
            ${error && html`<p class="error">${error}</p>`}
        <//>`;
}
