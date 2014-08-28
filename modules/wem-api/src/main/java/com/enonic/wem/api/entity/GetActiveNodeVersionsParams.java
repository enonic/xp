package com.enonic.wem.api.entity;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class GetActiveNodeVersionsParams
{
    private final EntityId entityId;

    private final ImmutableSet<Workspace> workspaces;

    private GetActiveNodeVersionsParams( Builder builder )
    {
        entityId = builder.entityId;
        workspaces = ImmutableSet.copyOf( builder.workspaces );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public ImmutableSet<Workspace> getWorkspaces()
    {
        return workspaces;
    }

    public static final class Builder
    {
        private EntityId entityId;

        private Set<Workspace> workspaces;

        private Builder()
        {
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder workspaces( Set<Workspace> workspaces )
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
