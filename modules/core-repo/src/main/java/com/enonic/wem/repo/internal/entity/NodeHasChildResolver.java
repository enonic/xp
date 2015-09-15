package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildResult;

public class NodeHasChildResolver
{
    private final QueryService queryService;

    private NodeHasChildResolver( Builder builder )
    {
        this.queryService = builder.queryService;
    }

    public NodesHasChildResult resolve( final Nodes nodes )
    {

        final NodesHasChildResult.Builder builder = NodesHasChildResult.create();

        for ( final Node node : nodes )
        {
            builder.add( node.id(), doResolve( node ) );
        }

        return builder.build();
    }

    public boolean resolve( final Node node )
    {
        return doResolve( node );
    }

    private boolean doResolve( final Node node )
    {
        final NodeQueryResult nodeQueryResult = this.queryService.find( NodeQuery.create().
            parent( node.path() ).
            countOnly( true ).
            build(), IndexContext.from( ContextAccessor.current() ) );

        return nodeQueryResult.getTotalHits() > 0;
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
