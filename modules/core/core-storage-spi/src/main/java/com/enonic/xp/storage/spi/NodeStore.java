package com.enonic.xp.storage.spi;

import java.util.Collection;
import java.util.List;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

/**
 * System-of-record operations for branch entries, versions and commits. Replaces the ES
 * {@code storage-<repo>} index (via {@code StorageDao}) for the node/repo layer above it.
 * Node/binary payloads stay on the existing {@code com.enonic.xp.blob.BlobStore} SPI and
 * are NOT part of this Phase-0 contract.
 * <p>
 * Contract notes (must hold for any implementation):
 * <ul>
 *   <li>{@link #storeBranchEntry} and {@link #storeVersion} are read-after-write
 *   consistent for a subsequent read against the same repo/branch.</li>
 *   <li>Node data, index-config and ACL blob keys use today's BlobKey format
 *   ({@code "sha256:<hex>"}) — unchanged, resolved through {@code BlobStore}.</li>
 * </ul>
 */
public interface NodeStore
{
    // --- branch entries (BRANCH document equivalent) ---

    void storeBranchEntry( RepositoryId repositoryId, Branch branch, BranchEntryRecord entry );

    void deleteBranchEntries( RepositoryId repositoryId, Branch branch, Collection<String> nodeIds );

    BranchEntryRecord getBranchEntry( RepositoryId repositoryId, Branch branch, String nodeId );

    BranchEntryRecord getBranchEntryByPath( RepositoryId repositoryId, Branch branch, String nodePath );

    List<BranchEntryRecord> getBranchEntries( RepositoryId repositoryId, Branch branch, Collection<String> nodeIds );

    List<BranchEntryRecord> getChildren( RepositoryId repositoryId, Branch branch, String parentNodePath, int from, int size );

    /** Branches containing the given node — replaces the cross-branch storage-index query. */
    List<Branch> getBranchesWithNode( RepositoryId repositoryId, String nodeId );

    // --- versions (VERSION document equivalent) ---

    void storeVersion( RepositoryId repositoryId, VersionRecord version );

    void deleteVersions( RepositoryId repositoryId, Collection<String> versionIds );

    VersionRecord getVersion( RepositoryId repositoryId, String versionId );

    List<VersionRecord> getVersions( RepositoryId repositoryId, Collection<String> versionIds );

    // --- commits (COMMIT document equivalent) ---

    void storeCommit( RepositoryId repositoryId, CommitRecord commit );

    CommitRecord getCommit( RepositoryId repositoryId, String commitId );
}
