package com.enonic.wem.core.workspace.compare.query;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class CompareEntityQuery
    extends AbstractCompareQuery
{

    private final EntityId entityId;

    private final Workspace source;

    private final Workspace target;

    private CompareEntityQuery( final Builder builder )
    {
        super( builder );
        entityId = builder.entityId;
        source = builder.source;
        target = builder.target;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }

    public static final class Builder
        extends AbstractCompareQuery.Builder<Builder>
    {
        private EntityId entityId;

        private Workspace source;

        private Workspace target;

        private Builder()
        {
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
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

        public CompareEntityQuery build()
        {
            return new CompareEntityQuery( this );
        }
    }
}
