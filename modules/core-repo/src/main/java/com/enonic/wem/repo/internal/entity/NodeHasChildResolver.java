package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class NodeHasChildResolver
{

    private final BranchService branchService;

    private NodeHasChildResolver( Builder builder )
    {
        this.branchService = builder.branchService;
    }

    public Nodes resolve( final Nodes nodes )
    {
        final Nodes.Builder populatedNodes = Nodes.create();

        for ( final Node node : nodes )
        {
            populatedNodes.add( doResolve( node ) );
        }

        return populatedNodes.build();
    }

    public Node resolve( final Node node )
    {
        return doResolve( node );
    }

    private Node doResolve( final Node node )
    {
        final boolean hasChildren = this.branchService.hasChildren( node.id(), InternalContext.from( ContextAccessor.current() ) );

        return Node.create( node ).hasChildren( hasChildren ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private BranchService branchService;

        private Builder()
        {
        }

        public Builder branchService( final BranchService branchService )
        {
            this.branchService = branchService;
            return this;
        }

        public NodeHasChildResolver build()
        {
            return new NodeHasChildResolver( this );
        }
    }
}
