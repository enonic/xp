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
 * <p>
 * {@code attributes} holds raw Java values (String/Long/Integer/Double/Boolean/List/Map,
 * see {@code GenericValue.toRawJava()}), not just strings, since node version attributes
 * are not string-only. A {@code null} map is distinct from an empty one: {@code null}
 * means no attributes were ever stored for this version (the underlying field is absent),
 * matching today's storage-doc behavior.
 */
@NullMarked
public record VersionRecord(String versionId, String nodeId, String nodePath, Instant timestamp, String nodeDataHash,
                             @Nullable String indexConfigHash, @Nullable String aclHash, List<String> binaryKeys,
                             @Nullable String commitId, @Nullable Map<String, Object> attributes)
{
    public VersionRecord
    {
        requireNonNull( versionId );
        requireNonNull( nodeId );
        requireNonNull( nodePath );
        requireNonNull( timestamp );
        requireNonNull( nodeDataHash );
        binaryKeys = List.copyOf( binaryKeys );
        attributes = attributes == null ? null : Map.copyOf( attributes );
    }
}
