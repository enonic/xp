package com.enonic.xp.storage.spi;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * VERSION document equivalent: immutable version metadata; payloads referenced by
 * content hash. Field set mirrors {@code VersionIndexPath} 1:1 so the mapping from
 * {@code VersionStorageDocFactory} is mechanical.
 */
@NullMarked
public record VersionRecord(String versionId, String nodeId, String nodePath, Instant timestamp, String nodeDataHash,
                             @Nullable String indexConfigHash, @Nullable String aclHash, List<String> binaryKeys,
                             @Nullable String commitId, Map<String, String> attributes)
{
    public VersionRecord
    {
        requireNonNull( versionId );
        requireNonNull( nodeId );
        requireNonNull( nodePath );
        requireNonNull( timestamp );
        requireNonNull( nodeDataHash );
        binaryKeys = List.copyOf( binaryKeys );
        attributes = Map.copyOf( attributes );
    }
}
