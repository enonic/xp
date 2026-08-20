package com.enonic.xp.node;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * A single node in an {@link EnumerateNodesResult}, identified without the node itself being read.
 * <p>
 * The version id names the version the enumeration observed, so a consumer that reads by it — such as
 * {@link NodeService#getByIdAndVersionId(NodeId, NodeVersionId)} or {@link NodeService#getVersion(NodeId, NodeVersionId)} — works on a
 * snapshot of the enumeration: the path and timestamp of the entry describe exactly what is read, also when the node has been written
 * to, moved or deleted since the entry was scanned.
 *
 * @param nodeId id of the node.
 * @param nodePath path of the node.
 * @param timestamp timestamp of the node.
 * @param versionId the version of the node the enumeration observed.
 * @since 8.1.0
 */
@NullMarked
public record NodeEnumerationEntry(NodeId nodeId, NodePath nodePath, Instant timestamp, NodeVersionId versionId)
{
    public NodeEnumerationEntry
    {
        requireNonNull( nodeId, "nodeId is required" );
        requireNonNull( nodePath, "nodePath is required" );
        requireNonNull( timestamp, "timestamp is required" );
        requireNonNull( versionId, "versionId is required" );
    }
}
