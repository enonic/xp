package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DeleteNodeResult
{
    private final ImmutableList<Result> results;

    private DeleteNodeResult( final Builder builder )
    {
        this.results = builder.builder.build();
    }

    public static Builder create()
    {
        return new DeleteNodeResult.Builder();
    }

    public NodeIds getNodeIds()
    {
        return results.stream().map( Result::nodeId ).collect( NodeIds.collector() );
    }

    public List<Result> getDeleted()
    {
        return results;
    }

    public static final class Builder
    {
        private ImmutableList.Builder builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final Result result ) {
            this.builder.add( result );
            return this;
        }

        public DeleteNodeResult build()
        {
            return new DeleteNodeResult( this );
        }
    }

    public record Result(NodeId nodeId, NodeVersionId nodeVersionId) {
    }
}
