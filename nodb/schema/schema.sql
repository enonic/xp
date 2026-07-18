-- NoDB Postgres schema, v0.3 (draft)
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
    repo_id    text NOT NULL UNIQUE,   -- external name; rename/swap = update this row
    settings   jsonb NOT NULL DEFAULT '{}',
    data       jsonb NOT NULL DEFAULT '{}',
    created_at timestamptz NOT NULL DEFAULT now()
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
    -- NOT FK-enforced against payload(hash) (Phase 1 correction, BUILD-PHASE-1.md Gate C):
    -- these three columns are content-hash REFERENCES, not necessarily rows in THIS
    -- tenant's payload table -- nodb's own writers (WriteBatch/bench) do populate payload
    -- via PutPayload first, but the Phase 1 XP integration keeps node data/index-config/ACL
    -- payloads on XP's existing BlobStore (file/S3) per design (a NoDB-backed BlobStore
    -- provider is the stretch gate), so hybrid-mode version writes carry hash values that
    -- were never inserted into this table. An FK here would reject every such write.
    node_data_hash    text NOT NULL,
    index_config_hash text NOT NULL,
    acl_hash          text NOT NULL,
    binary_keys       text[] NOT NULL DEFAULT '{}',  -- S3 blob keys (binaries stay in object store)
    commit_id         text,
    attributes        jsonb,                          -- {k,v} attribute list
    PRIMARY KEY (repo_key, version_id)                -- partition key must be in PK
) PARTITION BY LIST (repo_key);

CREATE INDEX node_version_by_node   ON node_version (repo_key, node_id, ts DESC);
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
