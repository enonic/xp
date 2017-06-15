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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Meta meta = (Meta) o;

        if ( current != meta.current )
        {
            return false;
        }
        if ( nodePath != null ? !nodePath.equals( meta.nodePath ) : meta.nodePath != null )
        {
            return false;
        }
        if ( timestamp != null ? !timestamp.equals( meta.timestamp ) : meta.timestamp != null )
        {
            return false;
        }
        if ( version != null ? !version.equals( meta.version ) : meta.version != null )
        {
            return false;
        }
        return nodeState == meta.nodeState;

    }

    @Override
    public int hashCode()
    {
        int result = nodePath != null ? nodePath.hashCode() : 0;
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        result = 31 * result + ( nodeState != null ? nodeState.hashCode() : 0 );
        result = 31 * result + ( current ? 1 : 0 );
        return result;
    }
}