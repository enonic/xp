package com.enonic.wem.core.entity;

import com.enonic.wem.api.workspace.Workspaces;

public class GetActiveNodeVersionsParams
{
    private final EntityId entityId;

    private final Workspaces workspaces;

    private GetActiveNodeVersionsParams( Builder builder )
    {
        entityId = builder.entityId;
        workspaces = builder.workspaces;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public Workspaces getWorkspaces()
    {
        return workspaces;
    }

    public static final class Builder
    {
        private EntityId entityId;

        private Workspaces workspaces;

        private Builder()
        {
        }

        public Builder entityId( final EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder workspaces( final Workspaces workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public GetActiveNodeVersionsParams build()
        {
            return new GetActiveNodeVersionsParams( this );
        }
    }
}
