package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import netscape.javascript.JSObject;
import sun.tools.jconsole.JConsole;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  private List<Service> services = new ArrayList<>();
  private DBConnector connector;
  private ServicePollerRepository repository;
  private BackgroundPoller poller;

  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);
    repository = new ServicePollerRepository(connector);
    poller = new BackgroundPoller(vertx);
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    repository.findAll().setHandler(res -> {
      if(res.succeeded()) {
        services.addAll(res.result());
        vertx.setPeriodic(5000, timerId -> poller.pollServices(services));
        setRoutes(router);
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(3000, result -> {
                  if (result.succeeded()) {
                    System.out.println("KRY code test service started");
                    startFuture.complete();
                  } else {
                    startFuture.fail(result.cause());
                  }
                });
      }
    });
  }

  private void setRoutes(Router router){
    // CORS HANDLING
    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("accept");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.OPTIONS);
    allowedMethods.add(HttpMethod.PUT);

    router.route().handler(CorsHandler.create("*")
            .allowedHeaders(allowedHeaders)
            .allowedMethods(allowedMethods));

    router.route("/*").handler(StaticHandler.create());
    router.get("/service").handler(req -> {
      List<JsonObject> jsonServices = services
          .stream()
          .map(service ->
              new JsonObject()
                  .put("name", service.getName())
                  .put("url", service.getUrl())
                  .put("status", service.getStatus())
                  .put("createdAt", service.getCreatedAt()))
          .collect(Collectors.toList());
      req.response()
          .putHeader("content-type", "application/json")
          .end(new JsonArray(jsonServices).encode());
    });
    router.post("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      Service service = new Service(jsonBody.getString("url"), jsonBody.getString("name"));
      service.setCreatedAt(LocalDate.now().toString());
      services.add(service);
      repository.save(service);
      req.response()
          .putHeader("content-type", "text/plain")
          .end(service.toJsonObject().encode());
    });
    router.delete("/service/:id").handler(req -> {
      int index = Integer.parseInt(req.request().getParam("id"));
      Service service = services.get(index);
      repository.delete(service);
      services.remove(service);
      req.response()
          .putHeader("content-type", "text/plain")
          .end("OK");
    });
  }

}



