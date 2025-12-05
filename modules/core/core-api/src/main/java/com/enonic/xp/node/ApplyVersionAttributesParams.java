package com.enonic.xp.node;

public final class ApplyVersionAttributesParams
{
    private final NodeVersionId nodeVersionId;

    private final Attributes addAttributes;

    private ApplyVersionAttributesParams( final Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.addAttributes = builder.addAttributes.build();
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public Attributes getAddAttributes()
    {
        return addAttributes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private final Attributes.Builder addAttributes = Attributes.create();

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            this.nodeVersionId = val;
            return this;
        }

        public Builder addAttributes( final Attributes val )
        {
            this.addAttributes.addAll( val.entrySet() );
            return this;
        }

        public ApplyVersionAttributesParams build()
        {
            return new ApplyVersionAttributesParams( this );
        }
    }
}
