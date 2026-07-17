package com.enonic.nodb.engine.model;

import java.time.Instant;

/**
 * BRANCH document equivalent (head pointer per (repo, branch, node)) — mirrors the
 * {@code branch_entry} table columns in schema.sql v0.3 exactly.
 *
 * <p>Deliberately narrower than {@code spi.Records.BranchEntryRecord}: the SPI draft
 * carries {@code nodeDataHash}/{@code indexConfigHash}/{@code aclHash} (inherited from
 * the ES-era BRANCH document, which duplicated those fields), but schema.sql's
 * {@code branch_entry} table does not store them — only {@code node_version} does. The
 * engine's own record mirrors the table it is actually persisted to; a future SPI
 * adapter in XP can join against {@link VersionRecord} to recover the hash fields if the
 * SPI shape is kept as-is.
 */
public record BranchEntryRecord(String branch, String nodeId, String versionId, String nodePath, Instant timestamp)
{
}
