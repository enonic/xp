package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceIdQuery
    extends AbstractWorkspaceQuery
{
    private final EntityId entityId;

    public WorkspaceIdQuery( final Workspace workspace, final EntityId entityId )
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
        if ( !( o instanceof WorkspaceIdQuery ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final WorkspaceIdQuery idQuery = (WorkspaceIdQuery) o;

        if ( entityId != null ? !entityId.equals( idQuery.entityId ) : idQuery.entityId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( entityId != null ? entityId.hashCode() : 0 );
        return result;
    }
}

