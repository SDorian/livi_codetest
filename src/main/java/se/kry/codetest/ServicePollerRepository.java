package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ServicePollerRepository {
	private DBConnector dbConnector;

	public ServicePollerRepository(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}


	public Future<List<Service>> findAll() {
		Future<List<Service>> services = Future.future();
		this.dbConnector.query("SELECT * FROM service").setHandler(res -> {
			if(res.succeeded()) {
				List<Service> list = new ArrayList<>();
				res.result().getRows().forEach(row -> list.add(new Service(row.getString("url"), row.getString("name"), row.getString("createdAt"))));
				services.complete(list);
			} else {
				services.fail(res.cause());
			}
		});

		return services;
	}

	//INSERT OR REPLACE INTO service (url, name, createdAt)" +
	//            " values (?,?,DATE('now','localtime')
	public void save(Service service) {
		this.dbConnector.query("INSERT OR REPLACE INTO service (url, name, createdAt) values (?, ?, DATE('now'))", new JsonArray().add(service.getUrl()).add(service.getName()));
	}

	public void delete(Service service) {
		this.dbConnector.query("DELETE FROM service WHERE url = ? AND name = ?", new JsonArray().add(service.getUrl()).add(service.getName()));
	}
}
