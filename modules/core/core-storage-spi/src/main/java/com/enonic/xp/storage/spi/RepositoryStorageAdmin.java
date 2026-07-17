package com.enonic.xp.storage.spi;

import java.util.Map;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;

/**
 * Storage-<repo> index lifecycle — the branch/version/commit system-of-record index behind
 * {@link NodeStore}. Replaces the storage-index half of {@code IndexServiceInternal}.
 * <p>
 * {@link #putIndexMapping} and {@link #getIndexSettings} are deliberately NOT scoped to the
 * storage index alone: some callers need cross-cutting per-repository settings/mapping
 * introspection regardless of which physical index a given {@link IndexType} lives in (e.g.
 * reading the replica count from the {@code VERSION} type to seed defaults for a brand-new
 * repository, or updating the {@code SEARCH} type's mapping in the same migration pass as
 * the storage types) — duplicating these two operations onto {@link NodeSearchIndex} just
 * for the {@code SEARCH} case would be pure duplication for no behavior difference.
 * <p>
 * ES-cluster-shaped concepts ({@code waitForYellowStatus}, {@code isMaster}, close/open
 * index, and the raw multi-repository bulk {@code deleteIndices} used by snapshot restore)
 * intentionally do NOT cross this SPI — they stay on the backend-internal
 * {@code IndexServiceInternal} (see DESIGN.md §3.2/§C).
 */
public interface RepositoryStorageAdmin
{
    void createIndex( RepositoryId repositoryId, IndexSettings settings, Map<IndexType, IndexMapping> mappings );

    void deleteIndex( RepositoryId repositoryId );

    boolean indexExists( RepositoryId repositoryId );

    /** Forces the storage-<repo> index to become read-visible for subsequent reads. */
    void refresh( RepositoryId repositoryId );

    void updateSettings( RepositoryId repositoryId, UpdateIndexSettings settings );

    void putIndexMapping( RepositoryId repositoryId, IndexType indexType, Map<String, Object> mapping );

    Map<String, String> getIndexSettings( RepositoryId repositoryId, IndexType indexType );
}
