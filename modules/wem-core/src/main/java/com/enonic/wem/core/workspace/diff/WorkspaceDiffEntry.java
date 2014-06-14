package com.enonic.wem.core.workspace.diff;

import com.enonic.wem.api.entity.EntityId;

class WorkspaceDiffEntry
{
    private final EntityId entityId;

    private final DiffStatus diffStatus;

    public WorkspaceDiffEntry( final EntityId entityId, final DiffStatus diffStatus )
    {
        this.entityId = entityId;
        this.diffStatus = diffStatus;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public DiffStatus getDiffStatus()
    {
        return diffStatus;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkspaceDiffEntry ) )
        {
            return false;
        }

        final WorkspaceDiffEntry that = (WorkspaceDiffEntry) o;

        if ( diffStatus != null ? !diffStatus.equals( that.diffStatus ) : that.diffStatus != null )
        {
            return false;
        }
        if ( entityId != null ? !entityId.equals( that.entityId ) : that.entityId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = entityId != null ? entityId.hashCode() : 0;
        result = 31 * result + ( diffStatus != null ? diffStatus.hashCode() : 0 );
        return result;
    }
}
