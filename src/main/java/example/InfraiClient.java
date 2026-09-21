package example;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public final class InfraiClient {
  private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
  private final String baseUrl;
  private final String key;

  public InfraiClient(String baseUrl, String key) { this.baseUrl = baseUrl; this.key = key; }

  public String request(String method, String path, String body) throws Exception {
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(baseUrl + path))
        .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + key)
        .header("Content-Type", "application/json");
    HttpRequest.BodyPublisher p = body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);
    HttpResponse<String> r = http.send(b.method(method, p).build(), HttpResponse.BodyHandlers.ofString());
    String json = r.body();
    if (!json.contains("\"ok\":true")) throw new IllegalStateException("Infrai rejected request: " + json);
    return json;
  }
}
