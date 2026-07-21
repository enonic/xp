-- NoDB tenant-schema template, derived from nodb/schema/schema.sql v0.3.
--
-- Applied by MigrationRunner with search_path set to the target tenant schema, so all
-- unqualified names below land inside that one schema. Contains ONLY the partitioned
-- parents and unpartitioned tables that exist once per tenant, at provisioning time.
--
-- NOT included here (deliberately): per-repo partitions of node_version/branch_entry
-- and per-branch sub-partitions of branch_entry. Those are runtime DDL created/dropped
-- by repo/branch lifecycle code (see schema.sql comments) — repo create = attach a
-- partition, repo delete = detach+drop, never migration-template concerns.
--
-- Indexes/constraints declared on the partitioned parents below propagate automatically
-- to partitions attached later.

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
    --
    -- Applied by editing THIS migration in place rather than adding a 002 migration
    -- (BUILD-PHASE-3.md Gate A scope item 1): the tenant schema is still pre-GA (every
    -- itest/bench run provisions fresh schemas via TenantProvisioner against a throwaway
    -- testcontainers Postgres; no tenant has ever been migrated in a durable environment),
    -- so there is no already-applied `001_init.sql` anywhere that needs a follow-up
    -- migration to catch up -- editing this file is simpler and mirrors the precedent Phase
    -- 1 Gate C itself set when it REMOVED this same FK by editing these two files directly,
    -- not by adding a migration. A 002 migration becomes the right tool once real tenants
    -- exist that must not be re-provisioned from scratch.
    node_data_hash    text NOT NULL REFERENCES payload (hash),
    index_config_hash text NOT NULL REFERENCES payload (hash),
    acl_hash          text NOT NULL REFERENCES payload (hash),
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
-- sub-partition per branch (sub-partitions are runtime DDL, see header comment).
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
