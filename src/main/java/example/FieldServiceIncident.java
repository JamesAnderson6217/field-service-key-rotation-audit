package example;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class FieldServiceIncident {
  private final InfraiClient client;
  public FieldServiceIncident(InfraiClient client) { this.client = client; }

  public String rotateTemporaryKey(String keyId) throws Exception {
    String capability = "account.keys.rotate";
    return client.request("POST", "/v1/account/keys/rotate/" + keyId,
        "{\"grace_hours\":24,\"idempotency_key\":\"rotate-" + keyId + "\"}");
  }

  public String reportLeak(String keyId) throws Exception {
    return client.request("POST", "/v1/account/keys/suspected_compromise/" + keyId,
        "{\"confirmed_leak\":true,\"auto_rotate\":false}");
  }

  public String recordBlastRadius(WorkOrder order) throws Exception {
    String event = "{\"message\":\"work_order_audit\",\"level\":\"info\",\"event\":\"work_order_audit\",\"work_order_id\":\"" + order.id()
        + "\",\"photo_url\":\"" + order.photoUrl() + "\",\"dispatch_status\":\""
        + order.dispatchStatus() + "\",\"technician_follow_up\":\"" + order.technicianFollowUp() + "\"}";
    return client.request("POST", "/v1/logs/ingest", "{\"entries\":[" + event + "]}");
  }

  public String searchBlastRadius(String workOrderId) throws Exception {
    return client.request("GET", "/v1/logs/search?q="
        + URLEncoder.encode("work_order_id=" + workOrderId, StandardCharsets.UTF_8), null);
  }
}
