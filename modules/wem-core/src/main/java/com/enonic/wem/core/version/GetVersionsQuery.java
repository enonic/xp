package com.enonic.wem.core.version;

import com.enonic.wem.api.entity.EntityId;

public class GetVersionsQuery
{
    private final Integer from;

    private final Integer size;

    private final EntityId entityId;

    private GetVersionsQuery( Builder builder )
    {
        from = builder.from;
        size = builder.size;
        entityId = builder.entityId;
    }

    public Integer getFrom()
    {
        return from;
    }

    public Integer getSize()
    {
        return size;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Integer from = 0;

        private Integer size = 10;

        private EntityId entityId;

        private Builder()
        {
        }

        public Builder from( Integer from )
        {
            this.from = from;
            return this;
        }

        public Builder size( Integer size )
        {
            this.size = size;
            return this;
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public GetVersionsQuery build()
        {
            return new GetVersionsQuery( this );
        }
    }
}
