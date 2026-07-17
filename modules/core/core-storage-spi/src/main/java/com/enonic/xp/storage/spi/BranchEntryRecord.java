package com.enonic.xp.storage.spi;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * BRANCH document equivalent: head pointer per (branch, node). Field set mirrors
 * {@code BranchIndexPath} 1:1 so the mapping from {@code BranchStorageRequestFactory} is
 * mechanical.
 */
@NullMarked
public record BranchEntryRecord(String nodeId, String nodePath, String versionId, String nodeDataHash, @Nullable String indexConfigHash,
                                 @Nullable String aclHash, Instant timestamp)
{
    public BranchEntryRecord
    {
        requireNonNull( nodeId );
        requireNonNull( nodePath );
        requireNonNull( versionId );
        requireNonNull( nodeDataHash );
        requireNonNull( timestamp );
    }
}
