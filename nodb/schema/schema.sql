-- NoDB Postgres schema, v0.5 (draft) — the summed content of the ordered tenant
-- migrations (001_init.sql + 002_version_query_indexes.sql + 004_snapshot_gc.sql;
-- 003_search_index.sql's search tables are documented in that migration file); kept
-- content-identical with them by hand.
--
-- Tenancy model:
--   * One SCHEMA per tenant (never one database per tenant: PG connection pools are
--     per-database, and pooling-per-cell is a core NoDB property).
--   * Repos are identified internally by a SURROGATE repo_key; the external repo id
--     ("fisk", "com.enonic.cms.default") is a mapping column in `repository`. All rows
--     and partitions bind to repo_key, so repo RENAME and atomic SWAP (side-by-side
--     restore -> verify -> swap) are single-row mapping updates — never row rewrites.
--   * Row-heavy tables (node_version, branch_entry) are LIST-partitioned by repo_key,
--     one partition per repo; branch_entry sub-partitions per branch. Repo/branch
--     delete = DETACH+DROP (instant, zero churn bloat); queries hit the logical parent
--     with partition pruning.
--   * payload (content pool) is deliberately tenant-shared and UNpartitioned:
--     per-tenant dedup across repos and cheap repo cloning depend on it.
--
-- Field sets mirror today's ES storage-<repo> documents 1:1
-- (BranchIndexPath / VersionIndexPath / CommitIndexPath).

CREATE TABLE repository (
    repo_key   bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    repo_id    text NOT NULL,          -- external name; rename/swap = update this row
    settings   jsonb NOT NULL DEFAULT '{}',
    data       jsonb NOT NULL DEFAULT '{}',
    created_at timestamptz NOT NULL DEFAULT now(),
    -- 004: repo lifecycle status for restore (Gate 0(c) state machine, consumed Gate B).
    status     text NOT NULL DEFAULT 'READY' CHECK (status IN ('READY', 'RESTORING', 'INDEXING')),
    -- 004: deferrable so the side-by-side swap can update two repo_id rows in one
    -- transaction (SET CONSTRAINTS ... DEFERRED) regardless of row order.
    CONSTRAINT repository_repo_id_key UNIQUE (repo_id) DEFERRABLE INITIALLY IMMEDIATE
);

CREATE TABLE branch (
    repo_key bigint NOT NULL REFERENCES repository (repo_key) ON DELETE CASCADE,
    branch   text NOT NULL,
    PRIMARY KEY (repo_key, branch)
);

-- Content-addressed node payloads: node data, index config, ACL JSON.
-- Keys keep today's BlobKey format ('sha256:<hex>') so dedup and dump formats carry over.
-- Tenant-shared pool: NOT per-repo (dedup + clone), NOT cross-tenant (existence oracle).
-- bytes is TOAST-compressed (default_toast_compression=lz4 recommended).
CREATE TABLE payload (
    hash       text PRIMARY KEY,
    bytes      bytea NOT NULL,
    byte_size  bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- VERSION document equivalent (immutable, append-only). Partition per repo.
CREATE TABLE node_version (
    repo_key          bigint NOT NULL,
    version_id        text NOT NULL,
    node_id           text NOT NULL,
    node_path         text NOT NULL,
    ts                timestamptz NOT NULL,
    -- FK RE-ENABLED (Phase 3 Gate A, BUILD-PHASE-3.md #10b): node/index-config/ACL
    -- payloads now live in THIS tenant's `payload` table for every write path -- Phase 3
    -- moved the node-payload segments off XP's BlobStore into NoDB (Gate 0's A-vs-B
    -- decision: WriteBatch is the sole writer), so "a version referencing a payload hash
    -- with no matching row" is no longer an expected hybrid-mode state, and the FK makes
    -- it structurally impossible rather than merely disciplined-by-convention.
    -- WriteService.write (and every other version-writing path) already inserts payload
    -- rows before the version row, in the same transaction, so this ordering was already
    -- required for correctness -- the FK just makes violating it fail loudly instead of
    -- silently. (Originally dropped in Phase 1 Gate C for the hybrid-mode window where XP
    -- kept these three segments on its own file/S3 BlobStore; that window is over.)
    node_data_hash    text NOT NULL REFERENCES payload (hash),
    index_config_hash text NOT NULL REFERENCES payload (hash),
    acl_hash          text NOT NULL REFERENCES payload (hash),
    binary_keys       text[] NOT NULL DEFAULT '{}',  -- S3 blob keys (binaries stay in object store)
    commit_id         text,
    attributes        jsonb,                          -- {k,v} attribute list
    PRIMARY KEY (repo_key, version_id)                -- partition key must be in PK
) PARTITION BY LIST (repo_key);

-- Version history (Phase 3.5): ts DESC with version_id ASC as the equal-ts tiebreaker,
-- so the keyset cursor (ts, version_id) is fully index-served. Replaced 001's
-- node_version_by_node (002_version_query_indexes.sql).
CREATE INDEX node_version_by_node_v2 ON node_version (repo_key, node_id, ts DESC, version_id ASC);
CREATE INDEX node_version_by_commit ON node_version (repo_key, commit_id) WHERE commit_id IS NOT NULL;
-- Vacuum support: "is this data row still referenced?" is an indexed lookup.
CREATE INDEX node_version_data_hash ON node_version (node_data_hash);
CREATE INDEX node_version_icfg_hash ON node_version (index_config_hash);
CREATE INDEX node_version_acl_hash  ON node_version (acl_hash);
-- Binary GC: GIN over binary keys replaces the blobstore mark-and-sweep.
CREATE INDEX node_version_binaries  ON node_version USING gin (binary_keys);

-- COMMIT document equivalent. Low volume; row-scoped is sufficient.
CREATE TABLE node_commit (
    commit_id text PRIMARY KEY,
    repo_key  bigint NOT NULL REFERENCES repository (repo_key),
    message   text,
    committer text,
    ts        timestamptz NOT NULL
);

-- Per-repo commit enumeration (Phase 3.5: FindCommits, RepoDumper's dump).
CREATE INDEX node_commit_by_repo ON node_commit (repo_key);

-- BRANCH document equivalent: head pointer per (repo, branch, node). Partition per repo,
-- sub-partition per branch.
CREATE TABLE branch_entry (
    repo_key    bigint NOT NULL,
    branch      text NOT NULL,
    node_id     text NOT NULL,
    version_id  text NOT NULL,
    node_path   text NOT NULL,
    parent_path text GENERATED ALWAYS AS (
                    CASE WHEN node_path = '/' THEN NULL
                         ELSE regexp_replace(node_path, '/[^/]*$', '')
                    END) STORED,
    ts          timestamptz NOT NULL,
    PRIMARY KEY (repo_key, branch, node_id),
    FOREIGN KEY (repo_key, branch) REFERENCES branch (repo_key, branch) ON DELETE CASCADE,
    FOREIGN KEY (repo_key, version_id) REFERENCES node_version (repo_key, version_id),
    UNIQUE (repo_key, branch, node_path)
) PARTITION BY LIST (repo_key);

-- Children listing / getByPath prefix ops (served from Postgres, strongly consistent).
CREATE INDEX branch_entry_children ON branch_entry (repo_key, branch, parent_path);
-- Branch diff / resolve-sync-work (Phase 3.5): per-side case-insensitive path-scope
-- prefix predicates (lower(node_path) = ... OR LIKE '.../%'). The unique path index
-- above cannot serve these: DB collation is en_US.utf8, and ES parity needs lower().
CREATE INDEX branch_entry_path_lower ON branch_entry (repo_key, branch, lower(node_path) text_pattern_ops);

-- Per-repo partitions, created at repo creation / dropped at repo deletion:
--   CREATE TABLE node_version_<repo_key>  PARTITION OF node_version  FOR VALUES IN (<repo_key>);
--   CREATE TABLE branch_entry_<repo_key>  PARTITION OF branch_entry  FOR VALUES IN (<repo_key>)
--     PARTITION BY LIST (branch);   -- branch_entry sub-partitions per BRANCH:
--   CREATE TABLE branch_entry_<repo_key>_<m> PARTITION OF branch_entry_<repo_key>
--     FOR VALUES IN ('<branch>');
-- Ephemeral-branch workflows (fork draft -> edit -> merge -> drop) are partition DDL:
--   fork  = create sub-partition + INSERT..SELECT narrow rows (payloads shared via hashes)
--   drop  = DETACH + DROP sub-partition (instant, zero bloat from churn)
-- Side-by-side repo restore: rows load under a NEW repo_key; swap = update the two
--   repo_id mapping rows in one transaction (search alias flips generation likewise).
-- Mega-repos can hash sub-partition node_version by node_id; invisible above SPI.

-- Transactional outbox feeding the OpenSearch indexer. Written in the same transaction
-- as branch_entry/node_version mutations. Unpartitioned: rows are short-lived (trimmed
-- past the checkpoint), and the feed must stay globally ordered per tenant.
CREATE TABLE outbox (
    seq        bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    repo_key   bigint NOT NULL,
    branch     text,
    node_id    text,
    version_id text,
    op         text NOT NULL,  -- INDEX | DELETE | DELETE_BRANCH | DELETE_REPO | REINDEX
    ts         timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE index_checkpoint (
    indexer    text PRIMARY KEY,      -- one row per index consumer
    seq        bigint NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- Platform audit: written ONLY by NoDB internals, in the SAME transaction as the
-- operation it records (atomic op+trail). Not a repo: unreachable from the node API,
-- append-only, unversioned. Read via management-plane RPC (admin scope). Rides the
-- tenant lifecycle (backup/migration/offboarding/deletion) because it lives here.
-- Control-plane events (token issuance, break-glass, plan changes) are mirrored in.
CREATE TABLE audit_log (
    seq       bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ts        timestamptz NOT NULL DEFAULT now(),
    subject   text NOT NULL,          -- control-plane account or service identity
    token_id  text,                   -- jti of the credential used
    scope     text NOT NULL,          -- runtime | operator | control-plane | ...
    action    text NOT NULL,          -- e.g. repo.restore, snapshot.create, token.issued
    resource  text,                   -- repo/branch/snapshot identifier
    detail    jsonb,
    prev_hash text                    -- optional hash chain: makes deletion provable
);

-- --- 004_snapshot_gc.sql (Phase 5 Gate A) ---------------------------------------------
-- Full rationale in the migration file; the summed final state:

-- Per-tenant retention policy (Phase 5 decision 2: the horizon is an explicit setting
-- with a stated default). Single row, seeded at provisioning.
CREATE TABLE retention_policy (
    singleton          boolean PRIMARY KEY DEFAULT true CHECK (singleton),
    snapshot_horizon   interval NOT NULL DEFAULT '30 days',
    gc_grace           interval NOT NULL DEFAULT '24 hours',
    version_age_floor  interval,                          -- NULL = keep all versions
    version_keep_min   int NOT NULL DEFAULT 1 CHECK (version_keep_min >= 1),
    outbox_retention   interval NOT NULL DEFAULT '7 days',
    updated_at         timestamptz NOT NULL DEFAULT now()
);
INSERT INTO retention_policy DEFAULT VALUES;

-- Snapshot registry. Snapshot BYTES (COPY streams + the FULL sorted hash manifest) live
-- in object storage under <tenant>/snapshot/<snapshot_id>/; Postgres holds only this row.
-- No FK to repository: a snapshot must survive the repo it was taken from.
CREATE TABLE snapshot (
    snapshot_id     text PRIMARY KEY,
    scope           text NOT NULL CHECK (scope IN ('REPO', 'TENANT')),
    repo_id         text,                                 -- NULL for TENANT scope
    repo_key        bigint,                               -- informational, never resolved on restore
    created_at      timestamptz NOT NULL DEFAULT now(),
    expires_at      timestamptz NOT NULL,                 -- created_at + snapshot_horizon AT CREATION
    outbox_seq      bigint NOT NULL,                      -- 0 while CREATING; stamped by COMPLETE
    state           text NOT NULL DEFAULT 'CREATING' CHECK (state IN ('CREATING', 'COMPLETE', 'FAILED')),
    location        text NOT NULL,                        -- object-storage prefix of the artifacts
    format_version  int NOT NULL DEFAULT 1,
    version_count   bigint,
    head_count      bigint,
    commit_count    bigint,
    document_count  bigint,
    hash_count      bigint,
    total_bytes     bigint,
    manifest_sha256 text
);
CREATE INDEX snapshot_by_repo ON snapshot (repo_id, created_at DESC);

-- GC mark table (mark phase / sweep phase with a persisted grace window; empty until
-- Gate C lands the mark/sweep logic and the mark-clear-on-reference write-path rule).
CREATE TABLE gc_mark (
    kind      text NOT NULL CHECK (kind IN ('PAYLOAD', 'BINARY')),
    hash      text NOT NULL,
    marked_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (kind, hash)
);
CREATE INDEX gc_mark_sweepable ON gc_mark (kind, marked_at);

-- Index generation counter that never reuses a number (Gate B consumes it). A table, not
-- a SEQUENCE: provisioning's default-privilege grants cover tables only.
CREATE TABLE index_generation (
    singleton boolean PRIMARY KEY DEFAULT true CHECK (singleton),
    last      int NOT NULL
);
INSERT INTO index_generation (last) VALUES (1000);
