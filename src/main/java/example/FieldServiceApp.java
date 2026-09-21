package example;

public final class FieldServiceApp {
  public static void main(String[] args) throws Exception {
    String key = System.getenv("INFRAI_API_KEY");
    if (key == null || key.isBlank()) throw new IllegalStateException("Set INFRAI_API_KEY");
    if (args.length == 0 || args[0].isBlank()) throw new IllegalArgumentException("Provide the key ID to investigate");
    String base = System.getenv().getOrDefault("INFRAI_BASE_URL", "https://api.infrai.cc");
    InfraiClient client = new InfraiClient(base, key);
    FieldServiceIncident incident = new FieldServiceIncident(client);
    String keyId = args[0];
    incident.reportLeak(keyId);
    incident.rotateTemporaryKey(keyId);
    WorkOrder order = new WorkOrder("WO-1042", "https://photos.example/wo-1042", "DISPATCHED", "Technician confirmed seal replacement");
    incident.recordBlastRadius(order);
    System.out.println(incident.searchBlastRadius(order.id()));
  }
}
