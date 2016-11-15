package com.enonic.xp.content;

import java.util.Collection;

import com.google.common.annotations.Beta;

@Beta
public class GetDependenciesResult
{
    private final Collection<ResolveDependenciesAggregationResult> inbound;

    private final Collection<ResolveDependenciesAggregationResult> outbound;

    private GetDependenciesResult( Builder builder )
    {
        this.inbound = builder.inboundDependencies;
        this.outbound = builder.outboudDependencies;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Collection<ResolveDependenciesAggregationResult> getInbound()
    {
        return inbound;
    }

    public Collection<ResolveDependenciesAggregationResult> getOutbound()
    {
        return outbound;
    }

    public static final class Builder
    {
        private Collection<ResolveDependenciesAggregationResult> outboudDependencies;

        private Collection<ResolveDependenciesAggregationResult> inboundDependencies;

        private Builder()
        {
        }

        public Builder inboundDependencies( final Collection<ResolveDependenciesAggregationResult> value )
        {
            this.inboundDependencies = value;
            return this;
        }

        public Builder outboudDependencies( final Collection<ResolveDependenciesAggregationResult> value )
        {
            this.outboudDependencies = value;
            return this;
        }

        public GetDependenciesResult build()
        {
            return new GetDependenciesResult( this );
        }
    }
}