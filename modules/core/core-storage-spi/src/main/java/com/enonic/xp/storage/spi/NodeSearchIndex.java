package com.enonic.xp.storage.spi;

import java.util.Collection;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

/**
 * Derived, rebuildable search index. Replaces the ES {@code search-<repo>} index (the
 * {@code SearchDao}/{@code StorageDao}-backed {@code IndexDataServiceImpl} write path, and
 * the search-index half of {@code IndexServiceInternal}).
 * <p>
 * {@link #search} also serves queries against the storage-<repo> index (branch/version/commit
 * listing and diffing) via {@link SingleRepoStorageSource} — the same generic
 * query/{@link SearchRequest} facility backs both physical indices today, so it is exposed
 * here as one operation rather than split across two SPI types.
 */
public interface NodeSearchIndex
{
    SearchResult search( SearchRequest searchRequest );

    void index( RepositoryId repositoryId, Branch branch, IndexDocumentRecord doc );

    void delete( RepositoryId repositoryId, Branch branch, Collection<String> nodeIds );

    ReturnValues get( RepositoryId repositoryId, Branch branch, String nodeId, ReturnFields returnFields,
                       @Nullable SearchPreference searchPreference );

    /** Forces the search-<repo> index to become read-visible for subsequent {@link #search}/{@link #get} calls. */
    void refresh( RepositoryId repositoryId );

    void createIndex( RepositoryId repositoryId, IndexSettings settings, IndexMapping mapping );

    void deleteIndex( RepositoryId repositoryId );

    boolean indexExists( RepositoryId repositoryId );

    void updateSettings( RepositoryId repositoryId, UpdateIndexSettings settings );
}
