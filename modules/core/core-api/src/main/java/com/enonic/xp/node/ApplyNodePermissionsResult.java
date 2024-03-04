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
    private final Map<NodeId, List<BranchResult>> branchResults;


    private ApplyNodePermissionsResult( Builder builder )
    {
        this.branchResults = (ImmutableMap) builder.branchResults.build().asMap();

    }

    public static Builder create()
    {
        return new Builder();
    }

    @Deprecated
    public Nodes getSucceedNodes()
    {
        return branchResults.values().stream().map( l -> l.get( 0 ).node ).filter( Objects::nonNull ).collect( Nodes.collecting() );
    }

    @Deprecated
    public Nodes getSkippedNodes()
    {
        return Nodes.empty();
    }

    public Map<NodeId, List<BranchResult>> getBranchResults()
    {
        return branchResults;
    }

    public Node getResult( final NodeId nodeId, final Branch branch )
    {
        final List<BranchResult> results = branchResults.get( nodeId );

        return results != null ? branchResults.get( nodeId )
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
        private final ImmutableListMultimap.Builder<NodeId, BranchResult> branchResults = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        @Deprecated
        public Builder succeedNode( final Node succeedNode )
        {
            return this;
        }

        @Deprecated
        public Builder skippedNode( final Node skippedNode )
        {
            return this;
        }

        public Builder addBranchResult( NodeId nodeId, Branch branch, Node node )
        {
            branchResults.put( nodeId, new BranchResult( branch, node ) );
            return this;
        }

        public ApplyNodePermissionsResult build()
        {
            return new ApplyNodePermissionsResult( this );
        }
    }
}
