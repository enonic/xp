package com.enonic.wem.core.version;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.NodeVersionId;

public class EntityVersionDocument
{
    private final NodeVersionId nodeVersionId;

    private final EntityId entityId;

    private EntityVersionDocument( final Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.entityId = builder.entityId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }


    public static class Builder
    {
        private NodeVersionId nodeVersionId;

        private EntityId entityId;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder entityId( final EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public EntityVersionDocument build()
        {
            validate();
            return new EntityVersionDocument( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( nodeVersionId );
            Preconditions.checkNotNull( entityId );
        }

    }


}
