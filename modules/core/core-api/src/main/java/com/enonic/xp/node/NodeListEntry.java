package com.enonic.xp.node;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * One node in a {@link ListNodesResult}, identified without reading the node itself.
 *
 * @since 8.1.0
 */
public record NodeListEntry(NodeId nodeId, NodePath nodePath, Instant timestamp)
{
    public NodeListEntry
    {
        requireNonNull( nodeId, "nodeId is required" );
        requireNonNull( nodePath, "nodePath is required" );
        requireNonNull( timestamp, "timestamp is required" );
    }
}
