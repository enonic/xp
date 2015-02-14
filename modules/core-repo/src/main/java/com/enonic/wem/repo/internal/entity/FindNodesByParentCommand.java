package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;

public class FindNodesByParentCommand
    extends AbstractNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodesByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public FindNodesByParentResult execute()
    {
        NodePath parentPath = params.getParentPath();
        if ( parentPath == null )
        {
            parentPath = getPathFromId( params.getParentId() );
            if ( parentPath == null )
            {
                return FindNodesByParentResult.create().nodes( Nodes.empty() ).totalHits( 0l ).hits( 0l ).build();
            }
        }

        final ChildOrder order = NodeChildOrderResolver.create().
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            nodePath( parentPath ).
            childOrder( params.getChildOrder() ).
            build().
            resolve();

        final NodeQuery query = createByPathQuery( order, parentPath );

        final NodeQueryResult nodeQueryResult = this.queryService.find( query, IndexContext.from( ContextAccessor.current() ) );

        final Nodes nodes = doGetByIds( nodeQueryResult.getNodeIds(), order.getOrderExpressions(), true );

        return FindNodesByParentResult.create().
            nodes( nodes ).
            totalHits( nodeQueryResult.getTotalHits() ).
            hits( nodeQueryResult.getHits() ).
            build();
    }

    private NodeQuery createByPathQuery( final ChildOrder order, final NodePath parentPath )
    {
        return NodeQuery.create().
            parent( parentPath ).
            query( new QueryExpr( order.getOrderExpressions() ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            countOnly( params.isCountOnly() ).
            build();
    }

    private NodePath getPathFromId( final NodeId nodeId )
    {
        final Context context = ContextAccessor.current();
        final NodeVersionId currentVersion = this.queryService.get( nodeId, IndexContext.from( context ) );
        if ( currentVersion == null )
        {
            return null;
        }
        final Node currentNode = nodeDao.getByVersionId( currentVersion );
        return currentNode == null ? null : currentNode.path();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public FindNodesByParentCommand build()
        {
            this.validate();
            return new FindNodesByParentCommand( this );
        }
    }
}
