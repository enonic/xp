package com.enonic.xp.node;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * A single node in a {@link NodeService#list(ListNodesParams)} listing, identified without the node itself being read.
 *
 * @param nodeId id of the node.
 * @param nodePath path of the node.
 * @param timestamp the moment the node was last modified in the branch listed.
 * @since 8.1.0
 */
@NullMarked
public record NodeListEntry(NodeId nodeId, NodePath nodePath, Instant timestamp)
{
    public NodeListEntry
    {
        requireNonNull( nodeId, "nodeId is required" );
        requireNonNull( nodePath, "nodePath is required" );
        requireNonNull( timestamp, "timestamp is required" );
    }
}
