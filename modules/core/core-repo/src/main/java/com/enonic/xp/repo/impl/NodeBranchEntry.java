package com.enonic.xp.repo.impl;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionMetadata;

public final class NodeBranchEntry
{
    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeId nodeId;

    private NodeBranchEntry( Builder builder )
    {
        this.nodeVersionId = Objects.requireNonNull( builder.nodeVersionId );
        this.nodeVersionKey = Objects.requireNonNull( builder.nodeVersionKey );
        this.nodePath = Objects.requireNonNull( builder.nodePath );
        this.timestamp = Objects.requireNonNull( builder.timestamp );
        this.nodeId = Objects.requireNonNull( builder.nodeId );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getVersionId()
    {
        return nodeVersionId;
    }

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static NodeBranchEntry fromNodeVersionMetadata( final NodeVersionMetadata nodeVersionMetadata ) {
        return NodeBranchEntry.create()
            .nodeId( nodeVersionMetadata.getNodeId() )
            .nodeVersionId( nodeVersionMetadata.getNodeVersionId() )
            .nodePath( nodeVersionMetadata.getNodePath() )
            .nodeVersionKey( nodeVersionMetadata.getNodeVersionKey() )
            .timestamp( nodeVersionMetadata.getTimestamp() )
            .build();
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeVersionKey nodeVersionKey;

        private NodePath nodePath;

        private Instant timestamp;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeVersionKey( final NodeVersionKey nodeVersionKey )
        {
            this.nodeVersionKey = nodeVersionKey;
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

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public NodeBranchEntry build()
        {
            return new NodeBranchEntry( this );
        }
    }
}
