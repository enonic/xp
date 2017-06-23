package com.enonic.xp.repo.impl.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;

public class StoreNodeVersionParams
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersion nodeVersion;

    private StoreNodeVersionParams( final Builder builder )
    {
        nodeVersion = builder.nodeVersion;
        timestamp = builder.timestamp;
        nodePath = builder.nodePath;
        nodeId = builder.nodeId;
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

    public NodeVersion getNodeVersion()
    {
        return nodeVersion;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeVersion nodeVersion;

        private Instant timestamp;

        private NodePath nodePath;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeVersion( final NodeVersion val )
        {
            nodeVersion = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public StoreNodeVersionParams build()
        {
            return new StoreNodeVersionParams( this );
        }
    }
}
