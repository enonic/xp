package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.EntityId;

public class WorkspaceIdQuery
    extends AbstractWorkspaceQuery
{
    private final EntityId entityId;

    private WorkspaceIdQuery( Builder builder )
    {
        super( builder );
        entityId = builder.entityId;
    }

    public static Builder create()
    {
        return new Builder();
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

    public static final class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private EntityId entityId;

        private Builder()
        {
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public WorkspaceIdQuery build()
        {
            return new WorkspaceIdQuery( this );
        }
    }
}

