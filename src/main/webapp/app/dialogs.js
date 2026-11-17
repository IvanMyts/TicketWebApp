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

export function IdInput({ value, onChange, required, placeholder }) {
    return html`<input type="number" min="1" step="1" placeholder=${placeholder || 'ID'} required=${required}
                       value=${value} onInput=${(e) => onChange(e.target.value)} />`;
}

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

function Input({ field, value, onChange }) {
    const change = (e) => onChange(e.target.value);
    if (field.ref) {
        return html`<${IdInput} value=${value} onChange=${onChange} />`;
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
    const values = {};
    for (const field of cfg.fields) {
        values[field.name] = '';
    }
    return values;
}

function getValue(item, name) {
    let value = item;
    for (const part of name.split('.')) {
        if (value === null || value === undefined) {
            return null;
        }
        value = value[part];
    }
    return value;
}

function toValues(cfg, item) {
    const values = {};
    for (const field of cfg.fields) {
        let value;
        if (field.ref) {
            const related = item[field.from];
            value = related ? related.id : null;
        } else {
            value = getValue(item, field.name);
        }
        if (value === null || value === undefined) {
            values[field.name] = '';
        } else if (field.type === 'datetime') {
            values[field.name] = String(value).slice(0, 16);
        } else {
            values[field.name] = String(value);
        }
    }
    return values;
}

function toRequest(cfg, values) {
    const request = {};
    for (const field of cfg.fields) {
        let value = values[field.name];
        if (value === '') {
            value = null;
        } else if (field.ref || field.type === 'int' || field.type === 'number') {
            value = Number(value);
        }
        const parts = field.name.split('.');
        if (parts.length === 2) {
            if (!request[parts[0]]) {
                request[parts[0]] = {};
            }
            request[parts[0]][parts[1]] = value;
        } else {
            request[field.name] = value;
        }
    }
    return request;
}

function validate(cfg, values) {
    for (const field of cfg.fields) {
        if (field.positive && values[field.name] !== '' && Number(values[field.name]) <= 0) {
            return `Поле «${field.label}» должно быть больше 0`;
        }
    }
    return '';
}

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
                        Если на объект ссылаются другие, укажите ID замены — ссылки перейдут на неё:
                        <${IdInput} value=${replacementId} onChange=${setReplacementId} placeholder="ID замены" />
                    </label>`}
                <div class="buttons">
                    <button class="danger" onClick=${remove}>Удалить</button>
                    <button onClick=${onClose}>Отмена</button>
                </div>`}
            ${error && html`<p class="error">${error}</p>`}
        <//>`;
}
