package com.enonic.xp.storage.spi;

import org.jspecify.annotations.NullMarked;

/**
 * Result of the branch-entry listing surface ({@link NodeStore#listChildEntries} /
 * {@link NodeStore#listBranchEntries}, nodb/BUILD-PHASE-4.md decision D2).
 *
 * <p>{@code entries} is deliberately an {@link Iterable} and not a {@link java.util.List}: a
 * whole-branch listing is unbounded (reindex walks every node of every branch), so the
 * implementation streams it in keyset-paged batches and the consumer sees one entry at a time.
 * {@code totalHits} is the up-front count the listing's consumers need BEFORE iterating —
 * {@code ReindexListener#branch(repositoryId, branch, size)} reports it, and the ES path
 * supplied it from the search response's total.
 *
 * <p><b>Iterating twice re-reads.</b> The iterable is a live cursor over the store, not a
 * materialized collection: a second {@code iterator()} issues the paging round trips again and
 * may legitimately observe concurrent writes. Consumers that need a stable snapshot materialize
 * it (both delete paths do — one bounded by a subtree, one an admin whole-branch operation).
 */
@NullMarked
public record BranchEntryListing(long totalHits, Iterable<BranchEntryRecord> entries)
{
    public static BranchEntryListing empty()
    {
        return new BranchEntryListing( 0, java.util.List.of() );
    }
}
