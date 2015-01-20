package com.enonic.wem.repo.internal.index.query;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.workspace.NodeWorkspaceState;

public class NodeWorkspaceVersion
{
    private final NodeVersionId nodeVersionId;

    private final NodeWorkspaceState state;

    private final NodePath nodePath;

    private NodeWorkspaceVersion( Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        state = builder.state;
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

    public NodeWorkspaceState getState()
    {
        return state;
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
        if ( state != that.state )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = nodeVersionId != null ? nodeVersionId.hashCode() : 0;
        result = 31 * result + ( state != null ? state.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeWorkspaceState state;

        private NodePath nodePath;

        private Builder()
        {
        }

        public Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder state( NodeWorkspaceState state )
        {
            this.state = state;
            return this;
        }

        public Builder nodePath( NodePath nodePath )
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
