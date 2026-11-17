import { html, useState, useEffect } from './lib.js';
import { api, setUnauthorizedHandler, subscribeToChanges } from './api.js';
import { ENTITIES } from './entities.js';
import { Login } from './login.js';
import { EntityPage } from './entity-page.js';
import { FormDialog, ViewDialog, DeleteDialog } from './dialogs.js';
import { Operations } from './operations.js';

function App() {
    const [user, setUser] = useState(undefined);
    const [page, setPage] = useState('tickets');
    const [dialog, setDialog] = useState(null);
    const [version, setVersion] = useState(0);

    useEffect(() => {
        setUnauthorizedHandler(() => setUser(null));
        api('auth/me').then((me) => setUser(me.login)).catch(() => setUser(null));
    }, []);

    useEffect(() => {
        if (!user) return undefined;
        return subscribeToChanges(() => setVersion((v) => v + 1));
    }, [user]);

    if (user === undefined) return html`<p class="center">Загрузка…</p>`;
    if (!user) return html`<${Login} onLogin=${setUser} />`;

    const open = (kind, type, id) => setDialog({ kind, type, id });
    const close = () => setDialog(null);
    const logout = async () => {
        await api('auth/logout', { method: 'POST' }).catch(() => {});
        setUser(null);
    };

    const menu = [];
    for (const key of Object.keys(ENTITIES)) {
        menu.push([key, ENTITIES[key].title]);
    }
    menu.push(['operations', 'Специальные операции']);

    return html`
        <nav class="menu">
            ${menu.map(([key, title]) => html`
                <button key=${key} class=${page === key ? 'active' : ''} onClick=${() => setPage(key)}>${title}</button>`)}
            <span class="spacer"></span>
            <span class="user">${user}</span>
            <button onClick=${logout}>Выход</button>
        </nav>
        <main>
            ${page === 'operations'
                ? html`<${Operations} open=${open} version=${version} />`
                : html`<${EntityPage} key=${page} type=${page} open=${open} version=${version} />`}
        </main>
        ${dialog && dialog.kind === 'view' && html`<${ViewDialog} type=${dialog.type} id=${dialog.id} open=${open} onClose=${close} version=${version} />`}
        ${dialog && dialog.kind === 'create' && html`<${FormDialog} type=${dialog.type} open=${open} onClose=${close} />`}
        ${dialog && dialog.kind === 'edit' && html`<${FormDialog} type=${dialog.type} id=${dialog.id} open=${open} onClose=${close} />`}
        ${dialog && dialog.kind === 'delete' && html`<${DeleteDialog} type=${dialog.type} id=${dialog.id} onClose=${close} />`}
    `;
}

ReactDOM.createRoot(document.getElementById('root')).render(html`<${App} />`);
