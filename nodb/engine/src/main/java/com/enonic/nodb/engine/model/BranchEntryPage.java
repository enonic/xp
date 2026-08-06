package com.enonic.nodb.engine.model;

import java.util.List;

/**
 * One page of a branch-entry walk (Phase 4 decision D2, nodb/BUILD-PHASE-4.md) — the storage
 * surface that replaces the three {@code NodeBranchQuery} call sites' reads of the ES
 * {@code storage-<repo>} index.
 *
 * @param entries         the page's rows, in the requested path order
 * @param nextAfterPath   {@code lower(node_path)} of the last row in {@code entries}, the
 *                        exclusive keyset continuation; empty when the page is empty
 * @param nextAfterNodeId the {@code node_id} tiebreaker of that same row — {@code node_path} is
 *                        unique per (repo, branch) today, but the keyset must be a TOTAL order
 *                        even if two rows ever collide on {@code lower(node_path)}
 * @param hasMore         whether a further row exists past this page. Determined by fetching one
 *                        row beyond the requested size, so a walk never needs a final empty
 *                        round trip to discover it has finished
 * @param totalHits       total rows matching the walk's predicate, or {@code -1} when not
 *                        requested. Only the first page pays for the count
 */
public record BranchEntryPage(List<BranchEntryRecord> entries, String nextAfterPath, String nextAfterNodeId, boolean hasMore,
                              long totalHits)
{
    public static final long NO_TOTAL = -1;

    public BranchEntryPage
    {
        entries = List.copyOf( entries );
    }
}
