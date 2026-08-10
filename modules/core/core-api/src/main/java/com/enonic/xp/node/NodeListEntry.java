package com.enonic.xp.node;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * A single node in a {@link ListNodesResult}, identified without the node itself being read.
 *
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
