let onUnauthorized = () => {};

export function setUnauthorizedHandler(handler) {
    onUnauthorized = handler;
}

export async function api(path, { method = 'GET', body, query } = {}) {
    let url = 'api/' + path;
    if (query) {
        const params = new URLSearchParams();
        for (const [key, value] of Object.entries(query)) {
            if (value !== null && value !== undefined && value !== '') params.set(key, value);
        }
        if (params.toString()) url += '?' + params;
    }

    const response = await fetch(url, {
        method,
        headers: method === 'GET' ? {} : { 'Content-Type': 'application/json' },
        body: body === undefined ? undefined : JSON.stringify(body),
    });

    if (response.status === 401 && !path.startsWith('auth/')) onUnauthorized();
    if (!response.ok) {
        let message = 'Ошибка ' + response.status;
        try {
            message = (await response.json()).message || message;
        } catch {
        }
        throw new Error(message);
    }
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

export function subscribeToChanges(onChange) {
    let socket;
    let stopped = false;
    let reconnecting = false;
    const connect = () => {
        const url = new URL('ws/changes', location.href);
        url.protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
        url.hash = '';
        socket = new WebSocket(url);
        socket.onopen = () => {
            if (reconnecting) onChange();
            reconnecting = false;
        };
        socket.onmessage = () => onChange();
        socket.onclose = async () => {
            if (stopped) return;
            reconnecting = true;
            const me = await fetch('api/auth/me').catch(() => null);
            if (stopped) return;
            if (me && me.status === 401) {
                onUnauthorized();
                return;
            }
            setTimeout(connect, 2000);
        };
    };
    connect();
    return () => {
        stopped = true;
        socket.close();
    };
}
