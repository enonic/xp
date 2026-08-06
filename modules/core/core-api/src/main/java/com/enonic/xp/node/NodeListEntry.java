package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

/**
 * One node in a {@link ListNodesByParentResult}: the data the branch holds about a node without reading the node itself.
 *
 * @since 8.1.0
 */
public final class NodeListEntry
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private NodeListEntry( final Builder builder )
    {
        this.nodeId = Objects.requireNonNull( builder.nodeId );
        this.nodePath = Objects.requireNonNull( builder.nodePath );
        this.timestamp = Objects.requireNonNull( builder.timestamp );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof NodeListEntry that && nodeId.equals( that.nodeId ) && nodePath.equals( that.nodePath ) &&
            timestamp.equals( that.timestamp );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeId, nodePath, timestamp );
    }

    @Override
    public String toString()
    {
        return nodePath + " [" + nodeId + "]";
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodePath nodePath;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public NodeListEntry build()
        {
            return new NodeListEntry( this );
        }
    }
}
