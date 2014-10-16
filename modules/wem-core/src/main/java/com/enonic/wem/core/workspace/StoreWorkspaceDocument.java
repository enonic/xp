package com.enonic.wem.core.workspace;

import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeVersionId;

public class StoreWorkspaceDocument
{
    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    private final NodePath path;

    private final NodePath parentPath;


    private StoreWorkspaceDocument( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodeVersionId = builder.nodeVersionId;
        this.parentPath = builder.parentPath;
        this.path = builder.path;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodePath getPath()
    {
        return path;
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        private NodePath path;

        private NodePath parentPath;

        public Builder path( final NodePath path )
        {
            this.path = path;
            return this;
        }

        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

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
        if ( parentPath != null ? !parentPath.equals( that.parentPath ) : that.parentPath != null )
        {
            return false;
        }
        if ( path != null ? !path.equals( that.path ) : that.path != null )
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
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = 31 * result + ( parentPath != null ? parentPath.hashCode() : 0 );
        return result;
    }
}
