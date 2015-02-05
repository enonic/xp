package com.enonic.wem.repo.internal.index.query;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeState;
import com.enonic.wem.api.node.NodeVersionId;

public class NodeWorkspaceVersion
{
    private final NodeVersionId nodeVersionId;

    private final NodeState nodeState;

    private final NodePath nodePath;

    private NodeWorkspaceVersion( Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        nodeState = builder.state;
        nodePath = builder.nodePath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getVersionId()
    {
        return nodeVersionId;
    }

    public NodeState getNodeState()
    {
        return nodeState;
    }

    public NodePath getNodePath()
    {
        return nodePath;
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

        final NodeWorkspaceVersion that = (NodeWorkspaceVersion) o;

        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }
        if ( nodeState != that.nodeState )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = nodeVersionId != null ? nodeVersionId.hashCode() : 0;
        result = 31 * result + ( nodeState != null ? nodeState.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeState state;

        private NodePath nodePath;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeState( final NodeState state )
        {
            this.state = state;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public NodeWorkspaceVersion build()
        {
            return new NodeWorkspaceVersion( this );
        }
    }
}
