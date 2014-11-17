package com.enonic.wem.repo.internal.workspace;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeVersionId;

public class StoreWorkspaceDocument
{
    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    private StoreWorkspaceDocument( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodeVersionId = builder.nodeVersionId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        public Builder id( final NodeId id )
        {
            this.nodeId = id;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public StoreWorkspaceDocument build()
        {
            return new StoreWorkspaceDocument( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof StoreWorkspaceDocument ) )
        {
            return false;
        }

        final StoreWorkspaceDocument that = (StoreWorkspaceDocument) o;

        if ( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null )
        {
            return false;
        }
        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + ( nodeVersionId != null ? nodeVersionId.hashCode() : 0 );
        return result;
    }
}
