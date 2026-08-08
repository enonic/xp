-- DRAFT (Phase 5 Gate 0 deliverable) — snapshot registry + GC bookkeeping + restore status.
--
-- NOT SHIPPED: this file lives under nodb/schema/draft/ and is NOT listed in
-- engine/src/main/resources/nodb/migrations/tenant/manifest.txt. Gate A moves it to
-- migrations/tenant/004_snapshot_gc.sql and appends it to the manifest (P3 discipline:
-- applied migrations are immutable and checksummed; new schema lands as a NEW migration).
--
-- Everything here is per-tenant (applied with search_path = tenant schema), like 001–003.

-- ---------------------------------------------------------------------------------------
-- 1. Per-tenant retention policy (BUILD-PHASE-5.md decision 2: the horizon is an explicit
--    per-tenant setting with a stated default — never an emergent property of when vacuum
--    last ran). Single row; GC and snapshot creation read it INSIDE the same transaction
--    that acts on it (P1 single-snapshot invariant), so a mid-run policy change cannot
--    produce a torn decision.
CREATE TABLE retention_policy (
    singleton          boolean PRIMARY KEY DEFAULT true CHECK (singleton),
    -- Decision-2 horizon: a payload/binary is collectible only when its GC mark is older
    -- than this (see gc_mark) — which proves no snapshot inside the horizon can reference
    -- it. Also stamps snapshot.expires_at at creation ("referential backups carry their
    -- expiry").
    snapshot_horizon   interval NOT NULL DEFAULT '30 days',
    -- Floor for the mark→sweep gap independent of the horizon; covers the staged binary
    -- upload window (risk #4: bytes durable on S3 before the referencing WriteBatch
    -- commits) and operator reaction time. Sweep condition uses
    -- GREATEST(snapshot_horizon, gc_grace).
    gc_grace           interval NOT NULL DEFAULT '24 hours',
    -- Version retention (vacuum): NULL = keep all versions (the default posture; XP's own
    -- default is P21D but NoDB does not silently adopt it — operator opt-in).
    version_age_floor  interval,
    -- Never trim a node below this many newest versions even when older than the floor.
    version_keep_min   int NOT NULL DEFAULT 1 CHECK (version_keep_min >= 1),
    -- Outbox rows below every consumer's checkpoint are trimmable; this window keeps them
    -- around anyway (ops forensics, future Phase-6 consumers registering late).
    outbox_retention   interval NOT NULL DEFAULT '7 days',
    updated_at         timestamptz NOT NULL DEFAULT now()
);
INSERT INTO retention_policy DEFAULT VALUES;

-- ---------------------------------------------------------------------------------------
-- 2. Snapshot registry. The manifest BYTES (row COPY streams + the full sorted hash list)
--    live in object storage under <tenant>/snapshot/<snapshot_id>/ — see Gate 0(b) report;
--    Postgres holds only this registry row. Deliberately NO FK to repository: a snapshot
--    must survive the repo it was taken from (restoring after a repo delete is the point).
CREATE TABLE snapshot (
    snapshot_id     text PRIMARY KEY,                    -- uuid, minted at create
    scope           text NOT NULL CHECK (scope IN ('REPO', 'TENANT')),
    repo_id         text,                                -- external name at snapshot time; NULL for TENANT scope
    repo_key        bigint,                              -- informational (the source partition), never resolved on restore
    created_at      timestamptz NOT NULL DEFAULT now(),
    -- created_at + retention_policy.snapshot_horizon AT CREATION TIME. Restore of a
    -- referential snapshot past this fails loudly up front (decision 2); a later horizon
    -- change does not retroactively bless old snapshots.
    expires_at      timestamptz NOT NULL,
    -- Outbox position captured inside the snapshot transaction (§3.3): the seq this
    -- snapshot is consistent with. Recorded for lag forensics and for change-feed
    -- consumers; restore does NOT replay outbox (search is rebuilt from the restored
    -- search_document rows).
    outbox_seq      bigint NOT NULL,
    state           text NOT NULL DEFAULT 'CREATING' CHECK (state IN ('CREATING', 'COMPLETE', 'FAILED')),
    -- Object-storage location prefix of the manifest artifacts (row streams + hash list).
    location        text NOT NULL,
    format_version  int NOT NULL DEFAULT 1,
    -- Verification + sizing (counts/bytes of the captured row sets and hash set).
    version_count   bigint,
    head_count      bigint,
    commit_count    bigint,
    document_count  bigint,
    hash_count      bigint,
    total_bytes     bigint,
    manifest_sha256 text                                 -- hash over the manifest artifacts, for restore integrity
);
CREATE INDEX snapshot_by_repo ON snapshot (repo_id, created_at DESC);

-- ---------------------------------------------------------------------------------------
-- 3. GC mark table (decision 1: mark phase and sweep phase with an explicit, PERSISTED
--    grace window between them; decision 2 via time arithmetic — see Gate 0(d) report for
--    the soundness argument, including WHY WriteService must delete marks for every hash
--    it (re-)references in the same write transaction).
--    Sweep rule, evaluated in ONE transaction with the delete (payloads) or with the mark
--    row delete (binaries):
--      marked_at < now() - GREATEST(snapshot_horizon, gc_grace)
--      AND the hash is (re-)verified unreferenced in that same snapshot.
--    Payloads additionally have the node_version FKs as a hard backstop: deleting a
--    still-referenced payload row fails in Postgres. Binaries (S3) have no FK; the mark
--    discipline is their only protection.
CREATE TABLE gc_mark (
    kind      text NOT NULL CHECK (kind IN ('PAYLOAD', 'BINARY')),
    hash      text NOT NULL,
    marked_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (kind, hash)
);
CREATE INDEX gc_mark_sweepable ON gc_mark (kind, marked_at);

-- ---------------------------------------------------------------------------------------
-- 4. Repo lifecycle status for restore (Gate 0(c) state machine).
--    READY     — normal operation.
--    RESTORING — row load in progress or partitions absent: ALL reads/writes for this repo
--                fail loudly (never silently empty).
--    INDEXING  — rows are correct and served from Postgres (get/getByPath/children OK);
--                search queries and refresh(SEARCH) wait or report status until the
--                BUILDING generation is replayed and the alias flipped.
ALTER TABLE repository ADD COLUMN status text NOT NULL DEFAULT 'READY'
    CHECK (status IN ('READY', 'RESTORING', 'INDEXING'));

-- ---------------------------------------------------------------------------------------
-- 5. Side-by-side swap support: the atomic swap updates two repository.repo_id values in
--    one transaction; a non-deferrable UNIQUE rejects the intermediate state depending on
--    row order, so the constraint becomes deferrable (checked at commit inside the swap
--    transaction via SET CONSTRAINTS ... DEFERRED).
ALTER TABLE repository DROP CONSTRAINT repository_repo_id_key;
ALTER TABLE repository ADD CONSTRAINT repository_repo_id_key UNIQUE (repo_id)
    DEFERRABLE INITIALLY IMMEDIATE;

-- ---------------------------------------------------------------------------------------
-- 6. Index generation counter that NEVER reuses a number (P1 HAZARDOUS-DEFERRED row:
--    generations restarting at 1 per repo incarnation make pre/post-swap deletes
--    indistinguishable and can PUT an index name that may still exist). Tenant-scoped
--    sequence: generation numbers are unique across all repos and all incarnations of a
--    repo, so a physical index name <tenant>-<repoId>+g<N> can never collide with a
--    half-deleted predecessor. A plain single-row table rather than a SEQUENCE because
--    TenantProvisioner's ALTER DEFAULT PRIVILEGES grant covers TABLES only
--    (TenantProvisioner.java:74-75) — a sequence would need a per-tenant USAGE grant the
--    provisioning machinery does not do. Allocation is
--    UPDATE index_generation SET last = last + 1 RETURNING last (atomic, serialized by
--    the row lock — correct under concurrent rebuilds by construction).
--    SearchIndexAdmin.nextGeneration switches to this; deleteIndex stops deleting history
--    (rows become state=RETIRED instead), and search_index.deleteAll remains only for
--    repo drop (ON DELETE CASCADE already handles that). Seeded past any generation an
--    existing pre-004 tenant can plausibly have reached.
CREATE TABLE index_generation (
    singleton boolean PRIMARY KEY DEFAULT true CHECK (singleton),
    last      int NOT NULL
);
INSERT INTO index_generation (last) VALUES (1000);
