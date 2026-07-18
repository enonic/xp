package com.enonic.nodb.engine.model;

import java.time.Instant;

/**
 * BRANCH document equivalent (head pointer per (repo, branch, node)) — mirrors the
 * {@code branch_entry} table columns in schema.sql v0.3 exactly, plus three fields
 * ({@code nodeDataHash}/{@code indexConfigHash}/{@code aclHash}) that are NOT columns of
 * {@code branch_entry} itself but are joined in from {@code node_version} on
 * {@code (repo_key, version_id)} by every {@link com.enonic.nodb.engine.store.BranchStore}
 * read method (Phase 1 Gate C N+1 fix, BUILD-PHASE-1.md) -- one SQL statement per read, no
 * follow-up {@code GetVersion} call needed by the XP client.
 *
 * <p>{@code spi.Records.BranchEntryRecord} (the ES-era BRANCH document shape the XP SPI
 * still uses) carries those same three fields; this record now matches it on reads. On the
 * write path ({@link com.enonic.nodb.engine.store.BranchStore#store}) they are irrelevant --
 * the {@code branch_entry} INSERT never touches them -- so the 5-arg constructor below
 * (used by every write-side caller) leaves them {@code null}; only the read-side 8-arg
 * constructor populates them.
 */
public record BranchEntryRecord(String branch, String nodeId, String versionId, String nodePath, Instant timestamp,
                                 String nodeDataHash, String indexConfigHash, String aclHash)
{
    /**
     * Write-path convenience constructor: hash fields are unused by {@code store()}/
     * {@code delete()}, so callers that only ever write (or the test suites that predate
     * the read-side JOIN) can keep constructing entries without them.
     */
    public BranchEntryRecord( String branch, String nodeId, String versionId, String nodePath, Instant timestamp )
    {
        this( branch, nodeId, versionId, nodePath, timestamp, null, null, null );
    }
}
