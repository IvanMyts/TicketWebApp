package websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Клиенты подключаются к ws://host/<context>/ws/changes и получают сообщения
 * {"entityType":"TICKET","id":5} при каждом изменении данных. Получив сообщение,
 * клиент сам перезапрашивает нужные данные по REST.
 * Неавторизованные подключения отсекает security.WebSocketAuthFilter ещё до установки соединения.
 */
@ServerEndpoint("/ws/changes")
public class ChangesEndpoint {
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        SESSIONS.remove(session);
    }

    static void broadcast(String message) {
        for (Session session : SESSIONS) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        }
    }
}
