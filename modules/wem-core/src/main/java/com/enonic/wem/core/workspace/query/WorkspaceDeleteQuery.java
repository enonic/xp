package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDeleteQuery
    extends AbstractWorkspaceQuery
{
    private EntityId entityId;

    public WorkspaceDeleteQuery( final Workspace workspace, final EntityId entityId )
    {
        super( workspace );
        this.entityId = entityId;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkspaceDeleteQuery ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final WorkspaceDeleteQuery that = (WorkspaceDeleteQuery) o;

        if ( !entityId.equals( that.entityId ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + entityId.hashCode();
        return result;
    }
}
