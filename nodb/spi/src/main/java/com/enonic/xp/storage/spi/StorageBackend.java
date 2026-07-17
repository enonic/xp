package com.enonic.xp.storage.spi;

/**
 * A storage backend for the XP node/repo layer. Replaces the four internal ES seams
 * (StorageDao, SearchDao, IndexServiceInternal, SnapshotService). Binaries remain on
 * the existing {@code com.enonic.xp.blob.BlobStore} SPI and are NOT part of this contract.
 *
 * Implementations: {@code storage-elasticsearch} (transitional, wraps today's embedded ES)
 * and {@code nodb-client} (gRPC client to a NoDB process — the only NoDB binding).
 */
public interface StorageBackend
{
    NodeStore nodeStore();

    NodeSearchIndex searchIndex();

    RepositoryStorageAdmin repositoryAdmin();

    SnapshotStore snapshots();
}
