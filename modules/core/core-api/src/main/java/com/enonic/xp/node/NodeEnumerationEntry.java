package com.enonic.xp.node;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * A single node in an {@link EnumerateNodesResult}, named without any of its data.
 * <p>
 * The version id is that of the version the enumeration observed. Reading the node by it — with
 * {@link NodeService#getByIdAndVersionId(NodeId, NodeVersionId)} or {@link NodeService#getVersion(NodeId, NodeVersionId)} — answers with
 * the state this entry describes: its path and timestamp are those of what is read, also where the node has been written to, moved or
 * deleted since.
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
