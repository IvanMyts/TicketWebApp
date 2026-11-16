package rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/** Все REST-ресурсы доступны по адресу /api/... */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
