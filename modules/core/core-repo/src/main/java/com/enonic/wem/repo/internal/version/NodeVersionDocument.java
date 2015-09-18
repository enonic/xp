package com.enonic.wem.repo.internal.version;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

public class NodeVersionDocument
{
    private final NodeVersionId nodeVersionId;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private NodeVersionDocument( final Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.nodeId = builder.nodeId;
        this.nodePath = builder.nodePath;
        this.timestamp = builder.timestamp;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
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

    public static class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeId nodeId;

        private NodePath nodePath;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
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

        public NodeVersionDocument build()
        {
            validate();
            return new NodeVersionDocument( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( nodeVersionId );
            Preconditions.checkNotNull( nodeId );
        }

    }


}
