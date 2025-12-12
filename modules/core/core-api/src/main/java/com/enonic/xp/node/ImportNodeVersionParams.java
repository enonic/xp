package com.enonic.xp.node;

import java.time.Instant;

public final class ImportNodeVersionParams
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersion nodeVersion;

    private final NodeVersionId nodeVersionId;

    private final NodeCommitId nodeCommitId;

    private final Attributes attributes;

    private ImportNodeVersionParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        nodeVersion = builder.nodeVersion;
        nodeVersionId = builder.nodeVersionId;
        nodeCommitId = builder.nodeCommitId;
        attributes = builder.attributes;
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

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public Attributes getAttributes()
    {
        return attributes;
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

        private NodeVersionId nodeVersionId;

        private NodeCommitId nodeCommitId;

        private Attributes attributes;

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

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder attributes( final Attributes val )
        {
            attributes = val;
            return this;
        }

        public ImportNodeVersionParams build()
        {
            return new ImportNodeVersionParams( this );
        }
    }
}
