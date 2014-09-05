package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class RenameNodeParams
{
    private final EntityId entityId;

    private final NodeName newNodeName;

    private RenameNodeParams( Builder builder )
    {
        entityId = builder.entityId;
        newNodeName = builder.newNodeName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public NodeName getNewNodeName()
    {
        return newNodeName;
    }

    public static final class Builder
    {
        private EntityId entityId;

        private NodeName newNodeName;

        private Builder()
        {
        }

        public Builder entityId( final EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder nodeName( final NodeName nodeName )
        {
            this.newNodeName = nodeName;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.entityId, "id cannot be null" );
            Preconditions.checkNotNull( this.newNodeName, "name cannot be null" );
        }

        public RenameNodeParams build()
        {
            this.validate();
            return new RenameNodeParams( this );
        }
    }
}
