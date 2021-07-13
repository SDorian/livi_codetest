package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

import java.util.List;

public class BackgroundPoller {
  private Vertx vertx;

  public BackgroundPoller(Vertx vertx) {
    this.vertx = vertx;
  }

  public void pollServices(List<Service> services) {
    services.parallelStream().forEach(this::retrieveStatus);
  }

  private void retrieveStatus(Service service) {
    WebClient.create(this.vertx).getAbs(service.getUrl()).send(response ->
      service.setStatus(response.succeeded() && 200 == response.result().statusCode() ?  "OK" : "FAIL")
    );
  }
}
