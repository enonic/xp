package com.enonic.xp.node;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class ApplyNodePermissionsResult
{
    private final Map<NodeId, List<BranchResult>> results;


    private ApplyNodePermissionsResult( Builder builder )
    {
        this.results = (ImmutableMap) builder.results.build().asMap();

    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<NodeId, List<BranchResult>> getResults()
    {
        return results;
    }

    public Node getResult( final NodeId nodeId, final Branch branch )
    {
        final List<BranchResult> results = this.results.get( nodeId );

        return results != null ? this.results.get( nodeId )
            .stream().filter( br -> br.branch.equals( branch ) ).map( BranchResult::getNode )
            .filter( Objects::nonNull )
            .findAny()
            .orElse( null ) : null;
    }

    public static final class BranchResult
    {
        private final Branch branch;

        private final Node node;

        public BranchResult( Branch branch, Node node )
        {
            this.branch = branch;
            this.node = node;
        }

        public Branch getBranch()
        {
            return branch;
        }

        public Node getNode()
        {
            return node;
        }

    }

    public static final class Builder
    {
        private final ImmutableListMultimap.Builder<NodeId, BranchResult> results = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        public Builder addResult( NodeId nodeId, Branch branch, Node node )
        {
            results.put( nodeId, new BranchResult( branch, node ) );
            return this;
        }

        public ApplyNodePermissionsResult build()
        {
            return new ApplyNodePermissionsResult( this );
        }
    }
}
