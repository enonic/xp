package com.enonic.wem.api.entity;

public class GetNodeVersionsParams
{
    private final EntityId entityId;

    private final int from;

    private final int size;

    private GetNodeVersionsParams( Builder builder )
    {
        entityId = builder.entityId;
        from = builder.from;
        size = builder.size;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static final class Builder
    {
        private EntityId entityId;

        private int from;

        private int size;

        private Builder()
        {
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public GetNodeVersionsParams build()
        {
            return new GetNodeVersionsParams( this );
        }
    }
}
