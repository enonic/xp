package com.enonic.xp.storage.spi;

import java.util.Collection;

/**
 * Derived, rebuildable search index. Replaces the ES {@code search-<repo>} index
 * (SearchDao + the search half of IndexServiceInternal).
 *
 * Consistency contract: implementations consume the NodeStore outbox. A committed
 * NodeStore write may lag here but can never be missed. {@link #awaitRefresh} implements
 * RefreshMode.SEARCH: block until the index has applied at least the given outbox
 * sequence, then make it visible to queries (engine refresh). Rebuild support
 * ({@link #reindex}) is required — it is the index-corruption recovery and the
 * search-engine major-upgrade path.
 *
 * SearchRequest/SearchResult carry the XP query AST (queries, aggregations, suggestions,
 * highlighting, sort, ACL filter) — translation to the engine DSL is backend-internal.
 */
public interface NodeSearchIndex
{
    void index( RepoRef repo, String branch, IndexDocumentRecord doc );

    void delete( RepoRef repo, String branch, Collection<String> nodeIds );

    SearchResult search( RepoRef repo, SearchRequest request );

    void awaitRefresh( RepoRef repo, long outboxSeq );

    /** Drop and rebuild the index for a branch from the NodeStore. Async; returns a task handle. */
    String reindex( RepoRef repo, String branch );

    void createIndex( RepoRef repo, IndexSettingsRecord settings );

    void deleteIndex( RepoRef repo );
}
