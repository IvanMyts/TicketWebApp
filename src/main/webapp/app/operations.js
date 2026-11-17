import { html, useState } from './lib.js';
import { api } from './api.js';
import { RefSelect } from './dialogs.js';

export function Operations({ open }) {
    return html`
        <h1>Специальные операции</h1>
        <${CountByNumber} />
        <${NamePrefix} open=${open} />
        <${DistinctNumbers} />
        <${SellTicket} open=${open} />
        <${CopyAsVip} open=${open} />`;
}

function useOperation() {
    const [result, setResult] = useState(null);
    const [error, setError] = useState('');
    const run = async (request) => {
        setError('');
        setResult(null);
        try {
            setResult(await request());
        } catch (e) {
            setError(e.message);
        }
    };
    return { result, error, run };
}

function Section({ title, onSubmit, children, result, error }) {
    return html`
        <section class="operation">
            <h2>${title}</h2>
            <form onSubmit=${(e) => { e.preventDefault(); onSubmit(); }}>
                ${children}
                <button type="submit">Выполнить</button>
            </form>
            ${error && html`<p class="error">${error}</p>`}
            ${result}
        </section>`;
}

function TicketLink({ ticket, open }) {
    return html`<a href="#" onClick=${(e) => { e.preventDefault(); open('view', 'tickets', ticket.id); }}>#${ticket.id} ${ticket.name}</a>`;
}

function CountByNumber() {
    const [number, setNumber] = useState('');
    const op = useOperation();
    return html`
        <${Section} title="Количество билетов, у которых number меньше заданного" error=${op.error}
                    onSubmit=${() => op.run(() => api('operations/count-number-less-than', { query: { number } }))}
                    result=${op.result && html`<p>Найдено билетов: <b>${op.result.count}</b></p>`}>
            <input type="number" step="any" required placeholder="число" value=${number} onInput=${(e) => setNumber(e.target.value)} />
        <//>`;
}

function NamePrefix({ open }) {
    const [prefix, setPrefix] = useState('');
    const op = useOperation();
    return html`
        <${Section} title="Билеты, у которых name начинается с подстроки" error=${op.error}
                    onSubmit=${() => op.run(() => api('operations/name-starts-with', { query: { prefix } }))}
                    result=${op.result && (op.result.length === 0
                        ? html`<p>Ничего не найдено</p>`
                        : html`<ul>${op.result.map((t) => html`<li key=${t.id}><${TicketLink} ticket=${t} open=${open} /></li>`)}</ul>`)}>
            <input required placeholder="начало названия" value=${prefix} onInput=${(e) => setPrefix(e.target.value)} />
        <//>`;
}

function DistinctNumbers() {
    const op = useOperation();
    return html`
        <${Section} title="Уникальные значения number" error=${op.error}
                    onSubmit=${() => op.run(() => api('operations/distinct-numbers'))}
                    result=${op.result && html`<p>${op.result.length === 0 ? 'Значений нет' : op.result.join(', ')}</p>`} />`;
}

function SellTicket({ open }) {
    const [ticketId, setTicketId] = useState('');
    const [personId, setPersonId] = useState('');
    const [price, setPrice] = useState('');
    const op = useOperation();
    const sell = () => op.run(async () => {
        if (Number(price) <= 0) throw new Error('Сумма должна быть больше 0');
        return api(`operations/tickets/${ticketId}/sell`, {
            method: 'POST',
            body: { personId: Number(personId), price: Number(price) },
        });
    });
    return html`
        <${Section} title="Продать билет за указанную сумму указанному человеку" error=${op.error} onSubmit=${sell}
                    result=${op.result && html`<p>Продан билет <${TicketLink} ticket=${op.result} open=${open} /> за ${op.result.price}</p>`}>
            <input type="number" min="1" required placeholder="ID билета" value=${ticketId} onInput=${(e) => setTicketId(e.target.value)} />
            <${RefSelect} type="persons" value=${personId} onChange=${setPersonId} required=${true} />
            <input type="number" step="any" required placeholder="сумма" value=${price} onInput=${(e) => setPrice(e.target.value)} />
        <//>`;
}

function CopyAsVip({ open }) {
    const [ticketId, setTicketId] = useState('');
    const op = useOperation();
    return html`
        <${Section} title="Скопировать билет как VIP с удвоенной ценой" error=${op.error}
                    onSubmit=${() => op.run(() => api(`operations/tickets/${ticketId}/copy-vip`, { method: 'POST' }))}
                    result=${op.result && html`<p>Создан билет <${TicketLink} ticket=${op.result} open=${open} />, цена ${op.result.price}</p>`}>
            <input type="number" min="1" required placeholder="ID билета" value=${ticketId} onInput=${(e) => setTicketId(e.target.value)} />
        <//>`;
}
