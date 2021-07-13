package se.kry.codetest;

import io.vertx.core.json.JsonObject;

public class Service {
	private String name;
	private String url;
	private String status;
	private String createdAt;

	public Service(String url, String name) {
		this.name = name;
		this.url = url;
		this.status = "UNKNOWN";
	}

	public Service(String url, String name, String createdAt) {
		this(url, name);
		this.createdAt = createdAt;
	}

	public Service(JsonObject jsonObject) {
		this.url = jsonObject.getString("url");
		this.name = jsonObject.getString("name");
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public JsonObject toJsonObject() {
		return new JsonObject()
					.put("name", this.name)
					.put("url", this.url)
					.put("status", this.status)
					.put("createdAt", this.createdAt);
	}
}
