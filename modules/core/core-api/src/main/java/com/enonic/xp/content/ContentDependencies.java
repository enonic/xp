package com.enonic.xp.content;

import java.util.Collection;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ContentDependencies
{
    private final Collection<ContentDependenciesAggregation> inbound;

    private final Collection<ContentDependenciesAggregation> outbound;

    private ContentDependencies( Builder builder )
    {
        this.inbound = builder.inboundDependencies;
        this.outbound = builder.outboundDependencies;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Collection<ContentDependenciesAggregation> getInbound()
    {
        return inbound;
    }

    public Collection<ContentDependenciesAggregation> getOutbound()
    {
        return outbound;
    }

    public static final class Builder
    {
        private Collection<ContentDependenciesAggregation> outboundDependencies;

        private Collection<ContentDependenciesAggregation> inboundDependencies;

        private Builder()
        {
        }

        public Builder inboundDependencies( final Collection<ContentDependenciesAggregation> value )
        {
            this.inboundDependencies = value;
            return this;
        }

        public Builder outboundDependencies( final Collection<ContentDependenciesAggregation> value )
        {
            this.outboundDependencies = value;
            return this;
        }

        public ContentDependencies build()
        {
            return new ContentDependencies( this );
        }
    }
}