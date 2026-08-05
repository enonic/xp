-- Phase 3.5 Gate A (nodb/BUILD-PHASE-3.5.md, Gate 0 DDL): indexes for the storage-index
-- query family served from Postgres -- branch diff, version history, per-repo commits.
-- All created on the partitioned parents, so they cascade to every existing and future
-- per-repo/per-branch partition.
--
-- The unique (repo_key, branch, node_path) index cannot serve the diff's path-prefix
-- scans: the database collation is en_US.utf8 (no text_pattern_ops/COLLATE "C"
-- anywhere), and ES parity requires case-insensitive comparison -- hence lower() +
-- text_pattern_ops.
CREATE INDEX branch_entry_path_lower ON branch_entry (repo_key, branch, lower(node_path) text_pattern_ops);

-- Replaces 001's node_version_by_node: history orders by ts DESC with version_id ASC as
-- the equal-ts tiebreaker, so the keyset cursor (ts, version_id) needs both columns in
-- the index, in exactly that order.
CREATE INDEX node_version_by_node_v2 ON node_version (repo_key, node_id, ts DESC, version_id ASC);
DROP INDEX node_version_by_node;

CREATE INDEX node_commit_by_repo ON node_commit (repo_key);
