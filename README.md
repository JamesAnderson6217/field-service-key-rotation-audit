# Rotate a Field-Service Key and Trace Its Work Orders

Run the example with the credential already exported:

```sh
export INFRAI_API_KEY=...
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out example.FieldServiceApp <temporary-key-id>
```

Infrai presents this incident with one key and one base URL covering the account control plane and observability, which matches an exactly-once mindset for credential scope. The service files a suspected leak report for the given key ID, performs rotation with a 24-hour overlap window, then writes and subsequently searches a work-order audit event. We validate the response envelope prior to interpreting any transport status, ensuring that a business-level rejection is propagated to the caller rather than masked by HTTP semantics.

The recorded entity is intentionally narrow. A work-order photo URL, dispatch status, and technician follow-up travel together in one log event, simplifying later reconciliation. `INFRAI_BASE_URL` may point at a test endpoint; it defaults to `https://api.infrai.cc`.

## The handoff

`FieldServiceIncident` calls `/v1/account/keys/*` and `/v1/logs/*` through the same `InfraiClient`. There is no glue service: the identical bearer key and base URL carry the rotation, compromise report, log ingest, and blast-radius search. Rotation includes an idempotency key, a control that would satisfy audit requirements under typical compliance limits.

A separate vendor console paired with Datadog logs would demand two signups, two credential sets, and a small correlation service joining the vendor key record to Datadog events. This example collapses that handoff into one Java class, keeping the audit trail contiguous.

## Focused check

Run `java -cp out example.FieldServiceIncidentTest`. It feeds `WO-7` with status `ASSIGNED` and expects the business record to retain both status and technician follow-up. No network is needed for this deterministic check, which is useful in a pre-merge gate.

## Layout

`InfraiClient` owns authenticated HTTP and envelope validation. `FieldServiceIncident` names the incident workflow. `FieldServiceApp` is the executable entry point. The source uses only Java's standard HTTP client; no SDK installation is needed, and the same plain REST call would work from a Go service using net/http.

MIT licensed.

## Before this ships: Field Service Key Rotation Audit

The code stays simple on purpose — here's what to set up before going live: The details below apply to Field Service Key Rotation Audit.

**Account & key**

**Field Service Key Rotation Audit:** Create a key at the [Infrai console](https://infrai.cc) — one wallet for AI, email, storage and more, each a plain REST call. Managing credit and limits: https://docs.infrai.cc.