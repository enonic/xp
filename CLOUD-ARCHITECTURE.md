# Enonic XP Cloud Architecture

**Status:** Draft v0.1

**Date:** 2026-07-29

**Companion document:** [`nodb/DESIGN.md`](nodb/DESIGN.md)

---

## 1. Purpose

This document describes the long-term architecture for operating Enonic XP efficiently
as a cloud platform while preserving the XP application model and the self-hosted
product.

The central shape is:

> A customer environment is one tenant. Each tenant has its own logical XP deployment,
> while storage and selected compute-intensive platform services are pooled in cells.

The first objective is not to run unrelated customer applications inside one XP JVM.
The first objective is to remove expensive per-environment infrastructure from that JVM,
make the runtime replaceable, and share the infrastructure that benefits from pooling.

`nodb/DESIGN.md` owns the storage data-plane design. This document places NoDB in the
larger cloud architecture and covers the XP runtime, deployment roles, control plane,
image processing, isolation tiers, scaling, and migration.

## 2. Architectural decisions

The following are the current target decisions:

1. **Tenant means customer environment**, not customer organization. Production,
   staging, and development are separate tenants.
2. **One logical XP deployment serves one tenant.** A deployment can contain zero, one,
   or many replicas and can be split into delivery, admin, and worker roles.
3. **An XP runtime credential is bound to exactly one NoDB tenant.** A tenant id supplied
   in an application request is never an authority boundary.
4. **NoDB is the shared content data plane.** PostgreSQL is the system of record,
   OpenSearch is a derived search projection, and S3-compatible storage holds binaries.
5. **Cells are the principal capacity and blast-radius unit.** A cell hosts a bounded set
   of tenants and the shared services serving them.
6. **Most tenants share a cell.** Large, noisy, regulated, or contractually isolated
   tenants can be moved to a dedicated cell without application changes.
7. **XP becomes disposable compute.** No authoritative customer data may exist only on
   an XP filesystem or in an XP process.
8. **Runtime roles are deployment profiles, not separate product forks.** The same XP
   codebase can run as delivery, admin, worker, or the traditional combined profile.
9. **Image transformation is a shared stateless cell service.** Expensive decode,
   resize, filter, and encode work does not require every XP runtime to reserve the same
   CPU capacity.
10. **Kubernetes is an implementation detail.** Users and operators interact with Enonic
    tenant, environment, and plan concepts through the control plane.
11. **Compatibility is the migration constraint.** Existing Node, Content, Portal, and
    JavaScript APIs remain stable while implementations move behind internal interfaces.
12. **Self-hosting remains a first-class topology.** Cloud pooling must not make the XP
    application model depend on Enonic-operated infrastructure.

## 3. Non-goals

- Rewriting XP as a collection of small microservices.
- Making the current OSGi JVM a hard multi-tenant execution boundary.
- Exposing Kubernetes, PostgreSQL, OpenSearch, or S3 concepts as the customer API.
- Changing XP application APIs merely to support cloud deployment.
- Requiring delivery, admin, and worker role separation in small self-hosted installs.
- Coupling NoDB correctness to the availability of the control plane.
- Introducing a second lightweight storage implementation for local development.

## 4. Terminology

### Organization

A commercial/customer account containing people, billing, and one or more environments.
Organization membership belongs to the control plane, not NoDB.

### Tenant

One isolated customer environment, for example `acme-production` or `acme-staging`.
It is the unit of:

- XP runtime credentials;
- content and binary isolation;
- backup, restore, export, and deletion;
- quotas, metering, and audit;
- cell placement and migration;
- application-set and runtime-version rollout.

### XP deployment

The logical compute deployment for one tenant. It may have multiple roles and replicas.
It is not synonymous with a VM or pod.

### Cell

A bounded failure, capacity, and operations domain containing:

- a stateless NoDB server fleet;
- a PostgreSQL cluster and connection pooler;
- an OpenSearch cluster or allocation domain;
- S3-compatible binary and derivative storage;
- a stateless image transformation fleet;
- cell-local ingress and observability components as required.

The control plane maps tenants to cells.

### Plan

The customer-facing combination of quotas, SLOs, features, and isolation level. Plans
do not expose infrastructure tuning knobs directly.

## 5. Target topology

```text
                              Control plane
            identity · desired state · placement · plans · upgrades
                                     |
                   +-----------------+-----------------+
                   |                                   |
            Tenant A deployment                Tenant B deployment
          +----------------------+            +----------------------+
          | delivery replicas    |            | combined XP replica  |
          | admin replicas       |            +----------+-----------+
          | worker replicas      |                       |
          +----------+-----------+                       |
                     | tenant-bound credentials          |
                     +----------------+-------------------+
                                      |
                              internal cell network
                                      |
          +---------------------------+---------------------------+
          |                           CELL                        |
          |                                                       |
          |  NoDB API fleet             Image service fleet       |
          |       |                          |                     |
          |       +-----------+--------------+                     |
          |                   |                                    |
          |        +----------+----------+                         |
          |        |                     |                         |
          |   PostgreSQL             OpenSearch                    |
          |        |                                               |
          |        +----------------- S3 --------------------------+
          +-------------------------------------------------------+
```

An organization with several environments is represented as several tenants:

```text
Acme organization
├── production tenant
│   └── dedicated XP deployment → shared or dedicated cell
├── staging tenant
│   └── smaller XP deployment → shared cell
└── development tenant
    └── scale-to-zero XP deployment → shared cell
```

## 6. XP runtime model

### 6.1 Tenant-bound deployment

One logical XP deployment serves one tenant. This remains true even if several
deployments share a Kubernetes cluster, host, cell, NoDB fleet, or image service.

The boundary provides:

- per-environment application and configuration lifecycle;
- per-environment secrets and workload identity;
- independent scaling, restart, and upgrade;
- heap, thread, and crash isolation between tenants;
- a clear place to apply resource requests, limits, and runtime policy;
- simple attribution of runtime cost.

The model does not prohibit a future pooled runtime for selected low-traffic tenants.
Such pooling is a later density optimization restricted to trusted, standardized
application sets. It is not the foundational security model.

### 6.2 Runtime roles

XP supports four deployment profiles from the same distribution:

| Profile | Responsibilities | Typical scaling signal |
|---|---|---|
| `delivery` | Portal rendering, public APIs, headless delivery | request concurrency, latency, CPU |
| `admin` | Content Studio, admin APIs, preview, interactive editing | active users, request latency |
| `worker` | scheduled jobs, imports, media work, application tasks | runnable work and oldest task age |
| `all` | current combined behavior | mixed; default for local and small self-hosted use |

Role separation is optional. A small tenant may use one combined instance. A larger
tenant can scale each role independently so that imports or scheduled work cannot
consume delivery capacity.

Role selection should be a supported XP configuration/profile with explicit capability
registration. It must not be maintained as separate distributions or source forks.

### 6.3 Disposable runtime requirements

An XP replica must be safe to stop and replace without copying its filesystem.

Authoritative state moves as follows:

| State | Long-term owner |
|---|---|
| content, branches, versions, ACL payloads | NoDB/PostgreSQL |
| search projection | NoDB/OpenSearch; rebuildable |
| source binaries | S3 through NoDB |
| generated image derivatives | image service/S3; rebuildable |
| application artifacts | versioned artifact registry |
| application-set desired state | control plane |
| environment configuration and secrets | control plane/secret manager |
| durable tasks and schedules | durable task store with leases |
| storage cache invalidation | NoDB change feed |
| transient local caches | XP local memory/disk; expendable |

Work needed to reach this state includes:

- deterministic startup from an immutable image plus desired state;
- readiness distinct from process liveness;
- graceful request draining and worker lease release;
- externally supplied secrets with rotation;
- an immutable/versioned application artifact contract;
- no runtime dependency on a persistent `XP_HOME`;
- bounded, disposable local caches;
- N/N-1 compatibility across rolling XP and NoDB upgrades.

### 6.4 Coordination and Hazelcast evacuation

NoDB's tenant change feed replaces Hazelcast for storage-event propagation and cache
invalidation. Other Hazelcast responsibilities require explicit replacements:

| Current concern | Long-term direction |
|---|---|
| storage change events | NoDB ordered tenant change feed |
| durable task submission/status | durable tenant task records |
| task placement | role/capability-aware worker claims |
| scheduler singleton behavior | database leases with fencing |
| web sessions | signed/stateless session where possible; external session store otherwise |
| node membership/reporting | orchestration and control-plane status |
| ephemeral in-process coordination | remain local where no distributed guarantee is needed |

Jobs must be idempotent or carry a clearly documented at-most-once requirement. Lease
expiry, retry, cancellation, duplicate delivery, and rolling deployment behavior are
part of the task contract rather than side effects of cluster membership.

## 7. NoDB content data plane

The detailed design and phased implementation live in [`nodb/DESIGN.md`](nodb/DESIGN.md).
At the cloud-architecture level, NoDB provides:

- authenticated, tenant-scoped gRPC APIs;
- PostgreSQL as the transactional system of record;
- OpenSearch as a derived and rebuildable projection;
- S3-compatible binary storage;
- a transactional outbox and change feed;
- tenant provisioning, backup, restore, dump/load, reindex, and vacuum;
- metering and quota enforcement at the shared resource boundary.

XP holds a NoDB endpoint and tenant-bound credential. It does not hold PostgreSQL,
OpenSearch, or S3 infrastructure credentials.

NoDB processes are stateless and scale independently from XP. The stores scale by their
native mechanisms. When a cell reaches a placement limit, the control plane adds a cell
or moves tenants.

## 8. Shared image transformation service

### 8.1 Motivation

Image generation is CPU- and memory-intensive but bursty. Reserving enough CPU on every
XP runtime to make cold transformations responsive produces poor fleet utilization.

The current image pipeline is already close to a service boundary:

- XP resolves content, permissions, attachment metadata, crop, focal point, and
  orientation;
- the transformation recipe is normalized;
- the source checksum and recipe produce an immutable cache key;
- decode, transform, and encode are deterministic for a fixed renderer version;
- generated results are rebuildable.

Image transformation therefore moves to a stateless, independently scaled pool in each
cell.

### 8.2 Responsibility boundary

XP retains:

- parsing the existing public image URL;
- resolving project, branch, content, and attachment;
- checking the requesting user's content permissions;
- resolving crop, focal point, orientation, output format, and cache policy;
- validating application-visible transformation parameters;
- constructing an authenticated normalized transformation request.

The image service owns:

- source retrieval by immutable binary hash;
- image header inspection and safety validation;
- decode, rotate, crop, scale, filter, background, and encode;
- CPU and memory admission control;
- concurrent-request collapsing;
- derivative storage, lookup, retention, and eviction;
- per-tenant transformation metering and fairness;
- returning bytes or a short-lived delivery URL.

The image service does not know XP content paths, projects, schemas, users, or ACL
semantics. It receives an already-authorized immutable source and recipe.

### 8.3 API shape

The internal API is versioned and conceptually contains:

```text
TransformImage
├── source binary hash
├── normalized crop
├── focal point
├── orientation
├── scale function and arguments
├── normalized filter list
├── output format and quality
├── background color
└── renderer profile/version
```

Tenant identity is derived from the authenticated caller credential. It is not trusted
from a request field.

Content id and path are deliberately absent. They are mutable CMS identifiers and are
not needed for deterministic transformation.

### 8.4 Cache and object layout

The derivative identity includes:

```text
SHA-256(
    tenant identity
    + source binary hash
    + canonical transformation recipe
    + renderer profile/version
)
```

An illustrative object layout is:

```text
<tenant>/image-derivatives/<renderer-version>/<hash>.<format>
```

The renderer version is mandatory. A library, encoder, scaling-algorithm, or default
change may produce different bytes from the same source and recipe.

Derivative deduplication is per tenant. Global cross-tenant deduplication is avoided
because it complicates erasure, retention, metering, isolation, and timing behavior.

Generated derivatives are immutable and disposable. Retention can be based on age and
storage pressure because any deleted derivative can be regenerated.

### 8.5 Data path

Image bytes should not take an unnecessary round trip through XP:

```text
S3 source → image worker → S3 derivative/CDN → client
```

Source access can use an object-scoped, short-lived capability issued through NoDB, or
an internal NoDB binary stream. The image service must not receive unrestricted
cross-tenant S3 access merely for convenience.

Delivery modes:

- **Public immutable image:** CDN/object delivery after XP has resolved the public URL.
- **Private image:** short-lived signed delivery URL or proxy through XP.
- **Initial compatibility mode:** XP may proxy the image-service response while CPU work
  and derivative caching have already moved out of the tenant runtime.

The existing XP image URL contract remains stable.

### 8.6 Request collapsing and failure behavior

Several XP replicas may request the same uncached derivative concurrently. The service
uses a distributed lease or equivalent fenced mechanism per derivative key:

```text
cache hit → return
cache miss → one worker generates
             concurrent callers wait for the same result
```

The final object write is conditional/idempotent. A local process lock is insufficient
because image workers scale horizontally.

Cache hits should remain available even when no image worker has spare generation
capacity. A cache miss that cannot be admitted returns an explicit retryable response;
XP maps it to the existing throttling behavior.

### 8.7 Safety and noisy-neighbor controls

The service enforces limits before and during decode:

- compressed source size;
- width, height, and total source pixels;
- maximum output dimensions and pixels;
- recipe and filter complexity;
- estimated peak decode/intermediate/output memory;
- processing deadline;
- per-tenant concurrent transformations;
- per-tenant CPU and output-byte budgets.

Autoscaling signals include:

- active and queued transformations;
- age of oldest admitted request;
- estimated admitted memory;
- CPU saturation;
- cache hit/miss ratio;
- transformation latency by operation and format.

A cell normally has a shared image pool. A dedicated image pool is an independent
placement option for sustained high-volume tenants; it does not require moving the
tenant to a dedicated NoDB cell.

### 8.8 Compatibility and engine evolution

The first remote implementation should use the current XP image engine and verify a
golden corpus against local `ImageServiceImpl`, including:

- output bytes where byte stability is promised;
- dimensions, orientation, crop, and focal-point behavior;
- output format, quality, alpha/background, and progressive encoding;
- validation and error mapping;
- malformed and oversized input behavior.

`ImageService` remains the XP-side seam:

- local provider for existing/default self-hosted behavior;
- remote gRPC provider for the cloud/cell topology.

A later engine such as libvips can be evaluated under a new renderer profile. It must
not silently change cached output under an existing renderer version.

## 9. Cell model and placement

### 9.1 Shared cell

The default cloud topology pools infrastructure while retaining tenant-specific XP
deployments:

```text
many tenant XP deployments
           ↓
shared NoDB and image-service fleets
           ↓
shared PostgreSQL, OpenSearch, and S3 cell
```

This captures most of the density benefit without treating the XP JVM as a hard
multi-tenant execution boundary.

### 9.2 Dedicated cell

A tenant can receive a dedicated cell for:

- regulatory or contractual isolation;
- data residency;
- customer-specific backup or disaster-recovery requirements;
- sustained database/search load;
- stronger performance isolation or SLO;
- customer-controlled networking or encryption;
- exceptional repository, shard, or storage scale.

“Enterprise” does not automatically mean “dedicated cell.” It is a plan capability and
placement decision. Many enterprise tenants can use a shared cell with dedicated XP
compute; some smaller regulated tenants may nevertheless require a dedicated cell.

### 9.3 Placement and capacity dimensions

Cell admission considers at least:

- PostgreSQL storage, write rate, connections, WAL, catalog relations, and vacuum load;
- OpenSearch shard count, index size, query load, and indexing lag;
- binary and derivative storage and egress;
- NoDB RPC rate, latency, and outbox lag;
- image CPU, memory, queue latency, and cache pressure;
- correlated tenant traffic and failure-domain limits.

Placement is controlled by measured headroom and policy. It is not an unlimited
first-fit bin-packing problem.

The three independent scaling verbs are:

1. Scale replicas for XP, NoDB, or image workers.
2. Scale the stores and cell infrastructure.
3. Add cells, move tenants, or promote a tenant to dedicated infrastructure.

## 10. Control plane

### 10.1 Responsibilities

The control plane owns desired state and fleet orchestration:

- organizations, people, roles, and service identities;
- tenant creation, suspension, offboarding, and deletion;
- tenant-to-cell placement and migration;
- XP version, application set, configuration, and runtime profile;
- domains, certificates, routing, and network policy;
- plans, entitlements, quotas, and billing aggregation;
- credential issuance, rotation, and revocation;
- rollout, rollback, maintenance, and restore workflows;
- fleet and schema upgrade orchestration;
- break-glass issuance and audit mirroring.

NoDB remains able to serve an already-provisioned tenant while the control plane is
temporarily unavailable. The control plane is not called on each content request.

### 10.2 Desired-state tenant resource

A conceptual tenant environment resource contains:

```yaml
identity:
  organization: acme
  environment: production
placement:
  class: shared
  region: eu-north
runtime:
  xpVersion: "..."
  applicationSet: "..."
  profiles:
    delivery: {min: 2, max: 10, size: medium}
    admin:    {min: 1, max: 3,  size: small}
    worker:   {min: 0, max: 5,  size: compute}
storage:
  plan: enterprise
image:
  plan: shared-standard
backup:
  policy: production
```

This is illustrative, not a commitment to expose Kubernetes-shaped fields to users.
Customer-facing plans may resolve to these internal settings.

### 10.3 Reconciliation

A controller reconciles tenant desired state into:

- NoDB tenant and credentials;
- XP deployments and role capabilities;
- image-service authorization and quotas;
- routing, DNS, and certificates;
- resource requests, limits, and autoscaling policy;
- observability, alerting, and billing attribution;
- backup and retention configuration.

All lifecycle actions are idempotent and auditable. Partial provisioning can be retried
without manual database surgery.

## 11. Security and isolation

### 11.1 Identity flow

- Control plane identities represent humans, organizations, and services.
- Each XP deployment receives a short-lived or rotatable credential for one tenant.
- NoDB and image service resolve tenant context only from authenticated identity.
- Infrastructure names and object prefixes are derived from that context.
- XP end-user identities remain XP concepts; they do not receive infrastructure
  credentials.

### 11.2 Defense in depth

Tenant isolation is applied independently at:

- workload identity and network policy;
- NoDB request context;
- PostgreSQL schema/role privileges;
- OpenSearch target construction and mandatory ACL filtering;
- S3 tenant prefixes and scoped capabilities;
- derivative cache namespace;
- quotas, concurrency, and fair scheduling;
- audit and metering attribution.

### 11.3 Runtime isolation tiers

| Tier | XP compute | Data plane |
|---|---|---|
| standard | tenant-specific deployment on shared compute cluster | shared cell |
| isolated compute | sandboxed or dedicated-node tenant deployment | shared cell |
| dedicated data plane | tenant-specific deployment | dedicated cell |
| self-hosted | customer-operated | customer-operated |

Future pooled XP execution, if introduced, is a separate lower-isolation tier limited
to controlled application sets.

## 12. QoS, quotas, and metering

Shared infrastructure requires admission control, not only after-the-fact metrics.

Per-tenant controls include:

- NoDB request concurrency and rate;
- database checkout concurrency and statement deadlines;
- search request and indexing budgets;
- binary ingress, egress, and stored bytes;
- image transformations, admitted memory, CPU time, and derivative bytes;
- background task concurrency;
- change-feed consumers and lag tolerance.

Runtime-class customer traffic can receive priority over interactive tooling and bulk
management operations. Chronic heavy tenants are resized, rate-shaped, or promoted to a
dedicated cell.

Billing counters are durable and auditable. Prometheus-style metrics are operational
signals and are not treated as the invoice ledger.

## 13. Observability and SLOs

Trace context propagates through:

```text
ingress → XP → NoDB/image gRPC → PostgreSQL/OpenSearch/S3
```

Telemetry carries tenant identity where cardinality and privacy permit, but avoids
unbounded repository/content labels.

Principal service indicators include:

### XP

- request rate, error rate, and latency by role;
- startup and readiness time;
- heap, CPU, thread, and local-cache behavior;
- running and queued application tasks.

### NoDB

- RPC latency/error by method;
- PostgreSQL pool checkout wait and transaction time;
- OpenSearch request latency and rejection;
- outbox age and entry lag;
- binary throughput and storage errors.

### Image service

- cache hit ratio;
- hit and miss response latency;
- queued and active transformations;
- estimated/admitted/rejected memory;
- transformation CPU duration;
- source and derivative bytes;
- failures by decoder, encoder, and limit.

### Cell

- placement headroom;
- tenant concentration;
- relation and shard counts;
- backup freshness and restore-drill result;
- storage growth and garbage-collection backlog.

SLOs distinguish cached image delivery from uncached transformation. A cell can have
healthy cached delivery while new transformations are temporarily rate-limited.

## 14. Topologies

### 14.1 Local development

```text
local XP (`all` profile)
        ↓ tenant credential
local `nodb dev`
        ├── managed PostgreSQL
        ├── managed OpenSearch
        └── local binary storage adapter
```

Starting an XP sandbox:

1. ensures `nodb dev` is running;
2. provisions or resolves one tenant;
3. supplies endpoint and tenant credential to XP;
4. starts the normal XP distribution.

The default image provider can remain local initially. A local image-service mode may
be added to exercise the cloud path without requiring Kubernetes.

### 14.2 Small self-hosted

```text
XP (`all`) → NoDB → customer PostgreSQL/OpenSearch/S3
```

The customer can use local image generation or run one image-service process. Role
splitting and a control plane are optional.

### 14.3 Shared cloud

```text
tenant-bound XP deployments → shared cell services
```

The control plane owns placement and lifecycle. Customers interact with environments
and plans, not cell internals.

### 14.4 Dedicated enterprise/regulated

```text
tenant-bound XP deployment → dedicated cell
```

The application and management APIs are identical to the shared topology.

## 15. Delivery roadmap

This roadmap complements, rather than replaces, the NoDB phases.

### Track A — Shared content data plane

Owned by [`nodb/DESIGN.md`](nodb/DESIGN.md):

1. storage SPI and compatibility boundary;
2. PostgreSQL node store and tenant model;
3. S3 binary path;
4. payload storage;
5. OpenSearch projection and query parity;
6. backup, restore, vacuum, dump/load, and retention;
7. control-plane integration, metering, QoS, and packaging;
8. migration and embedded-Elasticsearch deprecation;
9. structural-query optimizations.

### Track B — Image service

1. Freeze and document canonical image recipe and renderer-v1 behavior.
2. Extract the existing engine into a plain-Java component with golden-corpus tests.
3. Define tenant-authenticated gRPC API and remote `ImageService` provider.
4. Implement source retrieval and synchronous response without changing image URLs.
5. Add shared derivative storage and distributed request collapsing.
6. Add CDN/signed delivery, tenant QoS, metering, and autoscaling.
7. Add optional prewarming and evaluate a renderer-v2 engine.

### Track C — Disposable XP runtime

1. Inventory all durable/local/cluster state and startup dependencies.
2. Define immutable application artifact and application-set desired state.
3. Make local files and caches disposable.
4. Add supported `all`, `delivery`, `admin`, and `worker` profiles.
5. Move storage invalidation to the NoDB change feed.
6. Replace durable Hazelcast task/scheduler responsibilities with leased stores.
7. Externalize remaining session/coordination state where required.
8. Establish graceful draining, startup SLOs, and safe scale-to-zero behavior.

### Track D — Cloud control plane

1. Define tenant/environment desired-state and placement models.
2. Provision shared-cell tenants and tenant-bound credentials.
3. Reconcile XP runtime, routing, applications, and secrets.
4. Integrate NoDB and image-service quota/metering.
5. Implement upgrades, rollback, backup/restore, and migration workflows.
6. Add dedicated-cell promotion and tenant movement.
7. Expose customer administration and auditable break-glass operations.

### Recommended sequencing

The tracks can overlap, but production tenancy should be gated in this order:

1. NoDB search parity and measured performance.
2. Backup, restore drills, retention, vacuum, and garbage collection.
3. Shared-pool fairness, quotas, and security isolation tests.
4. Initial control-plane provisioning and cell placement.
5. Remote image service to reduce the XP per-tenant compute floor.
6. Disposable XP runtime and role profiles.
7. Pilot migrations with per-tenant rollback.
8. Scale-to-zero and advanced density optimizations.

## 16. Compatibility and migration

Cloud modernization uses progressive replacement:

- current and new providers coexist behind internal SPIs;
- existing XP application APIs and image URLs remain stable;
- default/self-hosted behavior changes only through explicit configuration until the
  replacement is mature;
- golden tests compare storage queries and image output;
- dump/load is the baseline data migration mechanism;
- selected tenants can use shadow reads and result comparison;
- deployments support per-tenant rollout and rollback;
- derived OpenSearch indices and image caches can be rebuilt;
- protocol and payload formats are versioned for rolling upgrades.

The migration is complete only when a tenant can be exported, restored into fresh
infrastructure, booted, searched, and served—including binaries—without access to the
old cell.

## 17. Risks and open questions

1. **XP-to-NoDB latency:** the existing API is chatty; caching, batching, and performance
   gates are adoption requirements.
2. **Cell fairness:** shared pools without per-tenant admission allow starvation even
   when data isolation is correct.
3. **OpenSearch density:** index-per-repository can create excessive shard overhead for
   many small tenants; shared-index modes require mandatory server-side routing.
4. **PostgreSQL catalog pressure:** tenant schemas and repository partitions require
   explicit relation-count capacity limits and migration orchestration.
5. **Runtime startup floor:** scale-to-zero is useful only after application activation
   and readiness latency are measured and improved.
6. **Task semantics:** moving from member execution to durable claims exposes implicit
   retry and idempotency assumptions in existing application tasks.
7. **Application artifacts:** cloud reconciliation needs immutable, reproducible
   application sets while XP currently supports dynamic installation behavior.
8. **Image compatibility:** a faster native image engine may not be byte- or
   pixel-identical to current Java output.
9. **Private image delivery:** signed CDN/object delivery must preserve XP permission and
   cache semantics without making the image service an ACL engine.
10. **Image request abuse:** arbitrary dimensions and filters can become a tenant-funded
    denial-of-service unless recipes and concurrency are bounded.
11. **Cross-service versioning:** XP, NoDB, image service, payload formats, and renderer
    profiles need an explicit compatibility matrix.
12. **Control-plane availability:** provisioning may pause during an outage, but existing
    tenants must continue serving from cell-local state.
13. **Tenant movement:** moving PostgreSQL, OpenSearch projection, binaries, derivatives,
    runtime routing, and change-feed cursors needs a tested state machine.
14. **Cost attribution:** shared fixed costs and burst capacity require a transparent
    allocation model that does not turn low-level metrics directly into confusing
    customer charges.

## 18. Definition of long-term success

The architecture has reached its intended state when:

- a developer can start a normal local XP instance against one local NoDB tenant and
  receive the full XP experience;
- the same XP distribution can attach to a cloud tenant using only endpoint and
  tenant-bound credentials;
- XP replicas can be replaced without persistent local state recovery;
- delivery, admin, and worker roles can scale independently or run combined;
- image-generation load no longer determines every XP tenant's minimum CPU size;
- a shared cell can host many isolated tenants with measured fairness and headroom;
- a tenant can move from a shared to dedicated cell without application changes;
- backup/restore, migration, upgrades, quotas, and audit operate at tenant scope;
- self-hosted customers retain a coherent, supported deployment;
- all of the above preserves the XP application-facing APIs and content semantics.
