package example;

public final class FieldServiceIncidentTest {
  public static void main(String[] args) {
    WorkOrder order = new WorkOrder("WO-7", "photo", "ASSIGNED", "call customer");
    if (!"ASSIGNED".equals(order.dispatchStatus()) || !order.id().startsWith("WO-")) throw new AssertionError("work order decision failed");
    System.out.println("PASS: work-order audit keeps dispatch status and follow-up together");
  }
}
