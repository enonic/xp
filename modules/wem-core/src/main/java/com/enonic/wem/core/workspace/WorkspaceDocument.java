package com.enonic.wem.core.workspace;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;

public class WorkspaceDocument
{
    private final EntityId entityId;

    private final NodeVersionId nodeVersionId;

    private final NodePath path;

    private final NodePath parentPath;

    private final Workspace workspace;

    private final Repository repository;

    private WorkspaceDocument( final Builder builder )
    {
        this.entityId = builder.entityId;
        this.nodeVersionId = builder.nodeVersionId;
        this.parentPath = builder.parentPath;
        this.path = builder.path;
        this.workspace = builder.workspace;
        this.repository = builder.repository;
    }

    public EntityId getEntityId()
    {
        return entityId;
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

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private EntityId entityId;

        private NodeVersionId nodeVersionId;

        private NodePath path;

        private NodePath parentPath;

        private Workspace workspace;

        private Repository repository;

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

        public Builder id( final EntityId id )
        {
            this.entityId = id;
            return this;
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder repository( final Repository repository )
        {
            this.repository = repository;
            return this;
        }

        public WorkspaceDocument build()
        {
            return new WorkspaceDocument( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkspaceDocument ) )
        {
            return false;
        }

        final WorkspaceDocument that = (WorkspaceDocument) o;

        if ( entityId != null ? !entityId.equals( that.entityId ) : that.entityId != null )
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
        if ( workspace != null ? !workspace.equals( that.workspace ) : that.workspace != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = entityId != null ? entityId.hashCode() : 0;
        result = 31 * result + ( nodeVersionId != null ? nodeVersionId.hashCode() : 0 );
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = 31 * result + ( parentPath != null ? parentPath.hashCode() : 0 );
        result = 31 * result + ( workspace != null ? workspace.hashCode() : 0 );
        return result;
    }
}
