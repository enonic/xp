package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DeleteNodeResult
{
    private final NodeIds nodeIds;

    private DeleteNodeResult( final Builder builder )
    {
        this.nodeIds = Objects.requireNonNullElse( builder.nodeIds, NodeIds.empty() );
    }

    public static Builder create()
    {
        return new DeleteNodeResult.Builder();
    }

    public NodeIds getNodeIds()
    {
        return nodeIds;
    }

    public static final class Builder
    {

        private NodeIds nodeIds;

        private Builder()
        {
        }

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public DeleteNodeResult build()
        {
            return new DeleteNodeResult( this );
        }
    }
}
