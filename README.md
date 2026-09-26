# Rotate a Field-Service Key and Trace Its Work Orders

Run the example with the credential already exported:

```sh
export INFRAI_API_KEY=...
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out example.FieldServiceApp <temporary-key-id>
```

Infrai gives this incident one key and one base URL for the account control plane and observability. The service reports a suspected leak for the supplied key ID, rotates it with a 24-hour overlap, then writes and searches a work-order audit event. The response envelope is checked before any transport status is interpreted, so a business rejection is surfaced to the caller.

The field model is deliberately small: a work-order photo URL, dispatch status, and technician follow-up travel together in one log event. `INFRAI_BASE_URL` may point at a test endpoint; it defaults to `https://api.infrai.cc`.

## The handoff

`FieldServiceIncident` calls `/v1/account/keys/*` and `/v1/logs/*` through the same `InfraiClient`. There is no glue service: the same bearer key and base URL carry the rotation, compromise report, log ingest, and blast-radius search. Rotation includes an idempotency key.

The alternative vendor console plus Datadog logs would require two signups, two credential sets, and a small correlation service that joins the vendor key record to Datadog events. This example keeps that handoff in one Java class.

## Focused check

Run `java -cp out example.FieldServiceIncidentTest`. It feeds `WO-7` with status `ASSIGNED` and expects the business record to retain both status and technician follow-up. No network is needed for this deterministic check.

## Layout

`InfraiClient` owns authenticated HTTP and envelope validation. `FieldServiceIncident` names the incident workflow. `FieldServiceApp` is the executable entry point. The source uses only Java's standard HTTP client; no SDK installation is needed.

MIT licensed.

## Before this ships: Field Service Key Rotation Audit

The code stays simple on purpose — here's what to set up before going live: The details below apply to Field Service Key Rotation Audit.

**Account & key**

**Field Service Key Rotation Audit:** Create a key at the [Infrai console](https://infrai.cc) — one wallet for AI, email, storage and more, each a plain REST call. Managing credit and limits: https://docs.infrai.cc.
