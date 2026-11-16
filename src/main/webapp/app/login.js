import { html, useState, useRef } from './lib.js';
import { api } from './api.js';

export function Login({ onLogin }) {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const form = useRef(null);

    const submit = async (register) => {
        if (!form.current.reportValidity()) return;
        setError('');
        try {
            const body = { login, password };
            if (register) await api('auth/register', { method: 'POST', body });
            const me = await api('auth/login', { method: 'POST', body });
            onLogin(me.login);
        } catch (e) {
            setError(e.message);
        }
    };

    return html`
        <form class="login" ref=${form} onSubmit=${(e) => { e.preventDefault(); submit(false); }}>
            <h1>Вход</h1>
            <label>Логин <input value=${login} required maxLength="255" onInput=${(e) => setLogin(e.target.value)} /></label>
            <label>Пароль <input type="password" value=${password} required maxLength="72"
                                 onInput=${(e) => setPassword(e.target.value)} /></label>
            ${error && html`<p class="error">${error}</p>`}
            <div class="buttons">
                <button type="submit">Войти</button>
                <button type="button" onClick=${() => submit(true)}>Зарегистрироваться</button>
            </div>
        </form>`;
}
