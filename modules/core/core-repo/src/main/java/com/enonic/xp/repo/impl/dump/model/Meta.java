package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;

public class Meta
{
    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersionId version;

    private final NodeState nodeState;

    private final boolean current;

    private Meta( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        nodeState = builder.nodeState;
        current = builder.current;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeVersionId getVersion()
    {
        return version;
    }

    public NodeState getNodeState()
    {
        return nodeState;
    }

    public boolean isCurrent()
    {
        return current;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath nodePath;

        private Instant timestamp;

        private NodeVersionId version;

        private NodeState nodeState;

        private boolean current;

        private Builder()
        {
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

        public Builder version( final NodeVersionId val )
        {
            version = val;
            return this;
        }

        public Builder nodeState( final NodeState val )
        {
            nodeState = val;
            return this;
        }

        public Builder current( final boolean val )
        {
            current = val;
            return this;
        }

        public Meta build()
        {
            return new Meta( this );
        }
    }
}