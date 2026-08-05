package com.enonic.xp.storage.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * System-of-record operations: branch entries, versions, commits and content-addressed
 * node payloads. Replaces the ES {@code storage-<repo>} index (via StorageDao) AND the
 * node-data segments of the blobstore (NodeVersionService) — in NoDB both live in Postgres.
 *
 * Contract notes (must hold for any implementation):
 * - {@link #storeBranchEntry} + {@link #storeVersion} + outbox emission are atomic per call
 *   batch: a committed write is either fully visible to reads or not at all.
 * - Reads (getBranchEntry*, children listing) are read-after-write consistent within a
 *   tenant. RefreshMode.STORAGE and per-request forceRefresh map to no-ops when the
 *   implementation is transactional.
 * - Node data is content-addressed with today's BlobKey format ("sha256:&lt;hex&gt;");
 *   storing identical bytes twice is a no-op (dedup preserved).
 */
public interface NodeStore
{
    // --- branch entries (BRANCH document equivalent) ---

    void storeBranchEntry( RepoRef repo, BranchEntryRecord entry );

    void deleteBranchEntries( RepoRef repo, String branch, Collection<String> nodeIds );

    BranchEntryRecord getBranchEntry( RepoRef repo, String branch, String nodeId );

    BranchEntryRecord getBranchEntryByPath( RepoRef repo, String branch, String nodePath );

    List<BranchEntryRecord> getBranchEntries( RepoRef repo, String branch, Collection<String> nodeIds );

    List<BranchEntryRecord> getChildren( RepoRef repo, String branch, String parentPath, Page page );

    /** Branches containing the given node — replaces the cross-branch storage-index query. */
    List<String> getBranchesWithNode( RepoRef repo, String nodeId );

    // --- versions (VERSION document equivalent) ---

    void storeVersion( RepoRef repo, VersionRecord version );

    void deleteVersions( RepoRef repo, Collection<String> versionIds );

    VersionRecord getVersion( RepoRef repo, String versionId );

    List<VersionRecord> findVersions( RepoRef repo, VersionQuery query );

    /** Active version per branch for one node, one round trip; absent branches simply missing (Phase 3.5). */
    Map<String, VersionRecord> getActiveVersions( RepoRef repo, String nodeId, Collection<String> branches );

    /**
     * Branch diff / resolve-sync-work (Phase 3.5): distinct ids of nodes in exactly one of
     * (source, target) or in both with different version ids; scope root included,
     * case-insensitive paths, scope/excludes per side, exact-path excludes.
     */
    List<String> diffBranches( RepoRef repo, String source, String target, String pathScope, Collection<String> excludes, int limit );

    // --- commits (COMMIT document equivalent) ---

    void storeCommit( RepoRef repo, CommitRecord commit );

    CommitRecord getCommit( RepoRef repo, String commitId );

    /** All commits of the repo (RepoDumper's dump enumeration, Phase 3.5). */
    List<CommitRecord> findCommits( RepoRef repo );

    // --- content-addressed payloads (node data / index config / ACL segments) ---

    /** Returns the content hash key ("sha256:..."), storing bytes only if unseen. */
    String putPayload( RepoRef repo, byte[] bytes );

    byte[] getPayload( RepoRef repo, String hash );
}
