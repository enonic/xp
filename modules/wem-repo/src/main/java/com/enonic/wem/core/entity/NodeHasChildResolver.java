package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.Nodes;

public class NodeHasChildResolver
{
    private final QueryService queryService;

    private NodeHasChildResolver( Builder builder )
    {
        this.queryService = builder.queryService;
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
        final boolean hasChildren = this.queryService.hasChildren( node.path(), IndexContext.from( ContextAccessor.current() ) );

        return Node.newNode( node ).hasChildren( hasChildren ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private QueryService queryService;

        private Builder()
        {
        }

        public Builder queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        public NodeHasChildResolver build()
        {
            return new NodeHasChildResolver( this );
        }
    }
}
