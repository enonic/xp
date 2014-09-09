package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.EntityId;

public class WorkspaceDeleteQuery
    extends AbstractWorkspaceQuery
{
    private EntityId entityId;

    private WorkspaceDeleteQuery( Builder builder )
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

    public static class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private EntityId entityId;

        private Builder()
        {
            super();
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public WorkspaceDeleteQuery build()
        {
            return new WorkspaceDeleteQuery( this );
        }
    }
}
