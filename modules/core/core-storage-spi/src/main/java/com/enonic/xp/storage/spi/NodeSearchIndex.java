package com.enonic.xp.storage.spi;

import java.util.Collection;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.query.Query;
import com.enonic.xp.repository.RepositoryId;

/**
 * Derived, rebuildable search index. Replaces the ES {@code search-<repo>} index
 * (SearchDao + the search half of IndexServiceInternal).
 * <p>
 * Phase-0-provisional: {@link #search} takes the XP query AST directly and returns a
 * minimal {@link SearchResultRecord}. The full {@code SearchRequest}/{@code SearchResult}
 * DTO family (return fields, search source, search preference, aggregations, suggestions,
 * highlighting) is already ES-free in core-repo and moves into this module as-is in
 * Gate C, at which point this method's signature is superseded.
 */
public interface NodeSearchIndex
{
    void index( RepositoryId repositoryId, Branch branch, IndexDocumentRecord doc );

    void delete( RepositoryId repositoryId, Branch branch, Collection<String> nodeIds );

    SearchResultRecord search( RepositoryId repositoryId, Branch branch, Query query );

    void refresh( RepositoryId repositoryId );

    void createIndex( RepositoryId repositoryId, IndexSettingsRecord settings );

    void deleteIndex( RepositoryId repositoryId );

    boolean indexExists( RepositoryId repositoryId );
}
