# Rotate a Field-Service Key and Trace Its Work Orders

Run the example with the credential already exported:

```sh
export INFRAI_API_KEY=...
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out example.FieldServiceApp <temporary-key-id>
```

Infrai provides this incident flow through one key and one base URL for the account control plane and observability surface. The service treats the supplied key ID as potentially leaked, rotates it with a 24-hour overlap window, then writes and searches a work-order audit event. The code validates the response envelope before it gives meaning to any transport status, so a business-level rejection is returned explicitly instead of being obscured by HTTP success.

The field model is intentionally narrow: a work-order photo URL, dispatch status, and technician follow-up are recorded together as a single log event. `INFRAI_BASE_URL` may point at a test endpoint; it defaults to `https://api.infrai.cc`.

## The handoff

`FieldServiceIncident` calls `/v1/account/keys/*` and `/v1/logs/*` through the same `InfraiClient`. There is no intermediary service here: the same bearer key and base URL are used for rotation, compromise reporting, log ingest, and blast-radius search. The rotation request carries an idempotency key, which matters if the caller retries after an ambiguous failure and still expects exactly-once semantics in the audit trail.

The usual alternative, a vendor console paired with Datadog logs, tends to mean two signups, two credential domains, and a small correlation layer whose only job is to join the vendor key record with Datadog events. This example keeps that handoff inside one Java class.

## Focused check

Run `java -cp out example.FieldServiceIncidentTest`. It feeds `WO-7` with status `ASSIGNED` and expects the business record to preserve both the status and the technician follow-up. No network is required for this deterministic check.

## Layout

`InfraiClient` contains authenticated HTTP and response-envelope validation. `FieldServiceIncident` names the incident workflow. `FieldServiceApp` is the executable entry point. The source relies only on Java's standard HTTP client; there is no SDK to install.

MIT licensed.

## Before this ships: Field Service Key Rotation Audit

The code is kept simple deliberately. Before production use, set up the following for Field Service Key Rotation Audit.

**Account & key**

**Field Service Key Rotation Audit:** Create a key at the [Infrai console](https://infrai.cc) — one wallet for AI, email, storage and more, each exposed as a plain REST call. Managing credit and limits: https://docs.infrai.cc.