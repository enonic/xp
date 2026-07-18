package com.enonic.xp.storage.spi;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

/**
 * System-of-record operations for branch entries, versions and commits. Replaces the ES
 * {@code storage-<repo>} index (via {@code StorageDao} and the {@code *StorageRequestFactory}
 * classes) for the node/repo layer above it. Node/binary payloads stay on the existing
 * {@code com.enonic.xp.blob.BlobStore} SPI and are NOT part of this Phase-0 contract.
 * <p>
 * Method set is deliberately scoped to exactly what {@code BranchServiceImpl},
 * {@code VersionServiceImpl} and {@code CommitServiceImpl} need (Gate B) — not a
 * speculative superset. Children listing and multi-repo search stay behind
 * {@link NodeSearchIndex} (Gate C).
 * <p>
 * Contract notes (must hold for any implementation):
 * <ul>
 *   <li>{@link #storeBranchEntry} and {@link #storeVersion} are read-after-write
 *   consistent for a subsequent read against the same repo/branch, EXCEPT
 *   {@link #getBranchEntryByPath}, which is served off a rebuildable index and is only
 *   consistent after a caller has forced a refresh (as today).</li>
 *   <li>Node data, index-config and ACL blob keys use today's BlobKey format
 *   ({@code "sha256:<hex>"}) — unchanged, resolved through {@code BlobStore}.</li>
 *   <li>{@code searchPreference} may be {@code null}, meaning "implementation default"
 *   (today: ES {@code _local}).</li>
 * </ul>
 */
@NullMarked
public interface NodeStore
{
    // --- branch entries (BRANCH document equivalent) ---

    void storeBranchEntry( RepositoryId repositoryId, Branch branch, BranchEntryRecord entry );

    void deleteBranchEntries( RepositoryId repositoryId, Branch branch, Collection<String> nodeIds );

    /** Existence check without fetching the entry's fields (today: no ES {@code _source} fetch). */
    boolean existsBranchEntry( RepositoryId repositoryId, Branch branch, String nodeId, @Nullable SearchPreference searchPreference );

    @Nullable
    BranchEntryRecord getBranchEntry( RepositoryId repositoryId, Branch branch, String nodeId,
                                       @Nullable SearchPreference searchPreference );

    /**
     * Path-based lookup. Unlike {@link #getBranchEntry}, this is served off a rebuildable
     * index rather than a real-time by-id read, so implementations must force a refresh of
     * that index before searching (as today) to preserve read-after-write consistency.
     */
    @Nullable
    BranchEntryRecord getBranchEntryByPath( RepositoryId repositoryId, Branch branch, String nodePath,
                                             @Nullable SearchPreference searchPreference );

    /** Returns only the entries found — missing ids are simply absent from the result, in no particular order. */
    List<BranchEntryRecord> getBranchEntries( RepositoryId repositoryId, Branch branch, Collection<String> nodeIds,
                                               @Nullable SearchPreference searchPreference );

    /** Branches containing the given node — replaces the cross-branch storage-index query. */
    List<Branch> getBranchesWithNode( RepositoryId repositoryId, String nodeId );

    /**
     * Storage-side children listing, ordered by node path (Phase 1 Gate C,
     * nodb/BUILD-PHASE-1.md's SPI&lt;-&gt;proto reconciliation table): NoDB's
     * {@code branch_entry.parent_path} generated column serves this directly, unlike ES's
     * storage index, which has never supported a path-prefix query (children listing has
     * always gone through {@link NodeSearchIndex}/{@code FindNodeIdsByParentCommand} for
     * every backend, ES included). Default method throws: only a backend whose storage
     * layer can actually answer this (nodb) overrides it. {@code parentPath} of {@code "/"}
     * denotes children of the root node.
     */
    default List<BranchEntryRecord> getChildren( RepositoryId repositoryId, Branch branch, String parentPath, int from, int size,
                                                   @Nullable SearchPreference searchPreference )
    {
        throw new UnsupportedOperationException(
            "getChildren is not supported by this NodeStore implementation; children listing goes through NodeSearchIndex instead (see this method's javadoc)" );
    }

    // --- versions (VERSION document equivalent) ---

    void storeVersion( RepositoryId repositoryId, VersionRecord version );

    void deleteVersion( RepositoryId repositoryId, String versionId );

    @Nullable
    VersionRecord getVersion( RepositoryId repositoryId, String versionId, @Nullable SearchPreference searchPreference );

    // --- commits (COMMIT document equivalent) ---

    void storeCommit( RepositoryId repositoryId, CommitRecord commit );

    @Nullable
    CommitRecord getCommit( RepositoryId repositoryId, String commitId, @Nullable SearchPreference searchPreference );
}
