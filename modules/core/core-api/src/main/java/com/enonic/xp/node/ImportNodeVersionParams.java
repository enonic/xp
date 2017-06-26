package com.enonic.xp.node;

import java.time.Instant;

public class ImportNodeVersionParams
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersion nodeVersion;

    private ImportNodeVersionParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        nodeVersion = builder.nodeVersion;
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
        private NodeId nodeId;

        private NodePath nodePath;

        private Instant timestamp;

        private NodeVersion nodeVersion;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public Builder nodeVersion( final NodeVersion val )
        {
            nodeVersion = val;
            return this;
        }

        public ImportNodeVersionParams build()
        {
            return new ImportNodeVersionParams( this );
        }
    }
}
