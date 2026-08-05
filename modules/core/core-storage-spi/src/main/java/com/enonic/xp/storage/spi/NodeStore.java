package com.enonic.xp.storage.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

/**
 * System-of-record operations for branch entries, versions and commits. Replaces the ES
 * {@code storage-<repo>} index (via {@code StorageDao} and the {@code *StorageRequestFactory}
 * classes) for the node/repo layer above it. Binary payloads stay on the existing
 * {@code com.enonic.xp.blob.BlobStore} SPI, out of scope here. Node payload segments
 * (node-data/index-config/ACL) rode {@code BlobStore} too through Phase 2 (hybrid), but
 * Phase 3 Gate B (nodb/BUILD-PHASE-3.md) moved their persistence into this contract — see
 * {@link #storeVersion} and {@link #storeNode} — so each backend can store them its own way
 * (elasticsearch: still {@code BlobStore}, relocated; nodb: the {@code payload} table, in
 * the same transaction as the version/branch-entry row).
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
 *   <li>Node data, index-config and ACL hashes use today's BlobKey format
 *   ({@code "sha256:<hex>"}) — unchanged.</li>
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

    /**
     * Stores a version together with its three payload segments (Phase 3 Gate B,
     * nodb/BUILD-PHASE-3.md — payloads used to live on {@code BlobStore}, written by
     * {@code NodeVersionServiceImpl} before this call; they now ride the SPI call itself so
     * a transactional backend can persist version + payloads as one atomic write). Segment
     * hashes MUST equal {@code version.nodeDataHash()}/{@code indexConfigHash()}/
     * {@code aclHash()} (caller invariant, not re-validated here). Each segment may be
     * hash-only ({@link PayloadSegment#bytes()} {@code null}) when the caller already knows
     * the content is stored (see {@link PayloadSegment}'s javadoc).
     */
    void storeVersion( RepositoryId repositoryId, VersionRecord version, NodeSegments segments );

    void deleteVersion( RepositoryId repositoryId, String versionId );

    @Nullable
    VersionRecord getVersion( RepositoryId repositoryId, String versionId, @Nullable SearchPreference searchPreference );

    // --- storage-index query family (Phase 3.5, nodb/BUILD-PHASE-3.5.md Gate A) ---
    //
    // These queries ran on the ES storage index (has_child over VERSION/BRANCH docs),
    // which a non-ES backend never creates — for them, storage-side SQL is the only path,
    // not an optimization. Default methods throw (the getChildren hook pattern): commands
    // route here only when supportsVersionQueries() says the backend can answer, otherwise
    // the legacy storage-index flow runs unchanged. No ACL filtering on any of them —
    // storage-source queries never filtered by ACL on the ES path either (parity).

    /**
     * Capability probe for the storage-index query family: {@code true} means
     * {@link #findVersions}, {@link #diffBranches}, {@link #getActiveVersions} and
     * {@link #findCommits} are implemented and commands may route to them instead of the
     * legacy storage-index flow. A probe on the injected instance, deliberately not a
     * configuration lookup (nodb/BUILD-PHASE-3.5.md Gate 0's routing-seam decision).
     */
    default boolean supportsVersionQueries()
    {
        return false;
    }

    /**
     * The bounded version-query surface: history-by-node (paged/ordered with keyset
     * cursor), dump/vacuum range scans and blob-key usage checks — see
     * {@link VersionQuery} for the exact option set and bound semantics.
     * {@link VersionQueryResult#totalHits} is accurate regardless of the paging window.
     */
    default VersionQueryResult findVersions( RepositoryId repositoryId, VersionQuery query )
    {
        throw new UnsupportedOperationException(
            "findVersions is not supported by this NodeStore implementation; version queries go through the search index instead (see this method's javadoc)" );
    }

    /**
     * Branch diff / resolve-sync-work: DISTINCT ids of nodes present in exactly one of
     * (source, target), or present in both with different version ids. Semantics pinned
     * by nodb/BUILD-PHASE-3.5.md Gate 0 (the ES {@code DiffQueryFactory} query is the
     * reference): the scope root itself IS included; path comparison is CASE-INSENSITIVE;
     * {@code pathScope} and {@code excludes} are evaluated PER SIDE (each side's rows
     * against that side's own paths — the rename-correct form); excludes match EXACT
     * paths only, never subtrees (what makes scope = parent, excludes = [parent] mean
     * "children of parent"). {@code pathScope} {@code null} = the whole branch;
     * {@code limit <= 0} = all ids, {@code limit} 1 = cheap existence-only probe
     * ({@code HasUnpublishedChildren}).
     */
    default List<String> diffBranches( RepositoryId repositoryId, Branch source, Branch target, @Nullable String pathScope,
                                        Collection<String> excludes, int limit )
    {
        throw new UnsupportedOperationException(
            "diffBranches is not supported by this NodeStore implementation; branch diff goes through the storage index instead (see this method's javadoc)" );
    }

    /**
     * Active version of one node per branch, ONE round trip — collapses the per-branch
     * {@link #getBranchEntry}+{@link #getVersion} loop. Branches where the node does not
     * exist are simply absent from the result.
     */
    default Map<Branch, VersionRecord> getActiveVersions( RepositoryId repositoryId, String nodeId, Collection<Branch> branches )
    {
        throw new UnsupportedOperationException(
            "getActiveVersions is not supported by this NodeStore implementation; use per-branch getBranchEntry/getVersion instead (see this method's javadoc)" );
    }

    /**
     * All commits of the repository ({@code RepoDumper}'s dump enumeration), in a
     * deterministic (timestamp, commit id) order.
     */
    default List<CommitRecord> findCommits( RepositoryId repositoryId )
    {
        throw new UnsupportedOperationException(
            "findCommits is not supported by this NodeStore implementation; commit enumeration goes through the storage index instead (see this method's javadoc)" );
    }

    /**
     * Combined store of a version, its payload segments, and its branch entry — the save
     * path used for every new/updated node ({@code NodeStorageServiceImpl#store}, via
     * {@code BranchService#storeWithVersion}). Semantically equivalent to
     * {@link #storeVersion} followed by {@link #storeBranchEntry} with the same arguments;
     * implementations backed by a single transactional store (nodb) MAY perform it as one
     * atomic write instead (nodb/BUILD-PHASE-3.md's "ONE WriteBatch per save" — the default
     * below is what a per-document backend like elasticsearch relies on, unchanged).
     */
    default void storeNode( RepositoryId repositoryId, Branch branch, NodeSegments segments, VersionRecord version,
                             BranchEntryRecord branchEntry )
    {
        storeVersion( repositoryId, version, segments );
        storeBranchEntry( repositoryId, branch, branchEntry );
    }

    // --- commits (COMMIT document equivalent) ---

    void storeCommit( RepositoryId repositoryId, CommitRecord commit );

    @Nullable
    CommitRecord getCommit( RepositoryId repositoryId, String commitId, @Nullable SearchPreference searchPreference );
}
