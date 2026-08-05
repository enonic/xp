-- Phase 4 Gate A (nodb/BUILD-PHASE-4.md): the OpenSearch indexer's durable state.
--
-- A NEW migration, never an edit to 001/002: gate P3 made applied migrations immutable and
-- checksummed, so schema added after it lands here by construction.
--
-- `outbox` and `index_checkpoint` already exist (001) and are UNCHANGED. What was missing is
-- the *content* the indexer applies. Decision 3 of the work order is that XP builds the index
-- documents and ships them, so the document is data NoDB must hold durably: without it the
-- outbox would be a list of "something changed" notes with nothing to apply, refresh(SEARCH)
-- could not be honoured after a restart, and Gate G's rebuild drill ("drop the index, replay,
-- get an identical index") would have nothing to replay. `search_document` is therefore the
-- system of record for search content until decision 3's later swap derives it server-side
-- from `payload` -- at which point this table becomes a cache and can be dropped in its own
-- migration.

-- The XP-shipped index document, one row per (repo, branch, node) -- exactly the granularity
-- of an OpenSearch document under the composite `_id` (`<nodeId>@<branch>`, see
-- SearchIndexNames/IndexDocumentProjection). Stored as the CANONICAL document XP shipped
-- (original flat dotted field names, XP's own postfix vocabulary), NOT the projected physical
-- document: the projection (`_text`/`_fulltext` renaming, ACL admin-key injection, collation
-- keys) is versioned and may change, and a projection bump must be replayable from the same
-- rows -- which is only true if what is stored is the input, not the output.
CREATE TABLE search_document (
    repo_key  bigint NOT NULL,
    branch    text NOT NULL,
    node_id   text NOT NULL,
    -- Canonical shipped document: {"<field>": [<value>, ...], ...} with type-tagged values.
    doc       jsonb NOT NULL,
    -- XP's per-document analyzer override (IndexDocumentRecord.analyzer); NULL = index default.
    analyzer  text,
    ts        timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (repo_key, branch, node_id),
    -- Repo delete (DELETE FROM repository) and branch delete both cascade through `branch`,
    -- so no search_document row can outlive the branch it belongs to. Unpartitioned on
    -- purpose: unlike branch_entry this is never range-scanned per branch in the hot path
    -- (the indexer looks rows up by exact key), and repo drop stays one cascading DELETE
    -- rather than another partition to detach.
    FOREIGN KEY (repo_key, branch) REFERENCES branch (repo_key, branch) ON DELETE CASCADE
);

-- Rebuild-from-docs (Gate G's drill) and DELETE_REPO/DELETE_BRANCH fan-out replay every row
-- of one repo (optionally one branch) in a deterministic order.
CREATE INDEX search_document_replay ON search_document (repo_key, branch, node_id);

-- The authoritative alias -> generation map (DESIGN.md §5: "Names are constructed one-way
-- from TenantContext; nothing correctness- or security-relevant ever parses a name back --
-- the authoritative alias→generation mapping is NoDB metadata"). One row per generation of
-- one repo's index; exactly one is LIVE (the one the alias points at).
--
-- template_version/projection_version are recorded per generation because both are part of
-- what the stored documents mean. The projection version is load-bearing for the Gate 0(b)
-- ACL finding: DESIGN §7.2 replaces ES's "no filter at all for role:system.admin" with the
-- indexer INJECTING role:system.admin into every document's read keys, so a document indexed
-- under an older projection silently vanishes from admin queries. Recording the version per
-- generation is what makes that detectable ("this generation was built by projection N") and
-- what makes the fix a generational rebuild rather than a silent partial reindex.
CREATE TABLE search_index (
    repo_key           bigint NOT NULL REFERENCES repository (repo_key) ON DELETE CASCADE,
    generation         int NOT NULL,
    alias_name         text NOT NULL,
    index_name         text NOT NULL,
    template_version   int NOT NULL,
    projection_version int NOT NULL,
    state              text NOT NULL,  -- LIVE | BUILDING | RETIRED
    created_at         timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (repo_key, generation),
    UNIQUE (index_name)
);

-- "Which generation serves this alias right now" is a single-row lookup on every index write.
CREATE INDEX search_index_live ON search_index (repo_key, state);
