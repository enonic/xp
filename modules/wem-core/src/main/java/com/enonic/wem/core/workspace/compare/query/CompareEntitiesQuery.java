package com.enonic.wem.core.workspace.compare.query;

import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;

public class CompareEntitiesQuery
{
    private final EntityIds entityIds;

    private final Workspace source;

    private final Workspace target;

    private CompareEntitiesQuery( Builder builder )
    {
        entityIds = EntityIds.from( builder.entityIds );
        source = builder.source;
        target = builder.target;
    }

    public EntityIds getEntityIds()
    {
        return entityIds;
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private EntityIds entityIds;

        private Workspace source;

        private Workspace target;

        private Builder()
        {
        }

        public Builder setEntityIds( final EntityIds entityIds )
        {
            this.entityIds = entityIds;
            return this;
        }

        public Builder source( Workspace source )
        {
            this.source = source;
            return this;
        }

        public Builder target( Workspace target )
        {
            this.target = target;
            return this;
        }

        public CompareEntitiesQuery build()
        {
            return new CompareEntitiesQuery( this );
        }
    }
}
