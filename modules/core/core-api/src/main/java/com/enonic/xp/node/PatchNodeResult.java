package com.enonic.xp.node;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class PatchNodeResult
{
    private final NodeId nodeId;

    private final List<BranchResult> results;

    private PatchNodeResult( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.results = builder.results.build();

    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public List<BranchResult> getResults()
    {
        return results;
    }

    public @Nullable Node getResult( final Branch branch )
    {
        return results.stream().filter( br -> br.branch.equals( branch ) ).findAny().map( BranchResult::node ).orElse( null );
    }

    public record BranchResult(Branch branch, Node node)
    {
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<BranchResult> results = ImmutableList.builder();

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder addResult( Branch branch, Node node )
        {
            results.add( new BranchResult( branch, node ) );
            return this;
        }

        public PatchNodeResult build()
        {
            return new PatchNodeResult( this );
        }
    }
}
