package com.enonic.wem.core.workspace.query;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;

public class WorkspaceIdsQuery
    extends AbstractWorkspaceQuery
{
    private EntityIds entityIds;

    private WorkspaceIdsQuery( final Builder builder )
    {
        super( builder );
        entityIds = builder.entityIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityIds getEntityIds()
    {
        return entityIds;
    }

    public Set<String> getEntityIdsAsStrings()
    {
        final Set<String> values = Sets.newLinkedHashSet();

        for ( final EntityId entityId : this.entityIds )
        {
            values.add( entityId.toString() );
        }

        return values;
    }

    public static final class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private EntityIds entityIds;

        private Builder()
        {
        }

        public Builder entityIds( EntityIds entityIds )
        {
            this.entityIds = entityIds;
            return this;
        }

        public WorkspaceIdsQuery build()
        {
            return new WorkspaceIdsQuery( this );
        }
    }
}
