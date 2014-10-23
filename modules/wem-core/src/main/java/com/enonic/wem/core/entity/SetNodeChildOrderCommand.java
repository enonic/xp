package com.enonic.wem.core.entity;

import java.util.LinkedHashSet;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;

public class SetNodeChildOrderCommand
    extends AbstractFindNodeCommand
{
    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private SetNodeChildOrderCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.childOrder = builder.childOrdrer;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node parentNode = doGetNode( nodeId, false );

        final boolean newOrderingIsManual = childOrder.isManualOrder();
        final boolean childrenAreUnordered = !parentNode.getChildOrder().isManualOrder();
        final boolean childrenMustBeOrdered = newOrderingIsManual && childrenAreUnordered;

        if ( childrenMustBeOrdered )
        {
            orderChildNodes( parentNode );
        }

        final Node editedNode = Node.editNode( parentNode ).childOrder( childOrder ).build();

        doStoreNode( editedNode );

        return doGetNode( editedNode.id(), false );
    }

    private void orderChildNodes( final Node parentNode )
    {
        final NodeQueryResult childNodeResult = queryService.find( NodeQuery.create().
            parent( parentNode.path() ).
            query( new QueryExpr( parentNode.getChildOrder().getChildOrderExpressions() ) ).
            build(), IndexContext.from( Context.current() ) );

        final LinkedHashSet<NodeId> childNodeIds = childNodeResult.getNodeQueryResultSet().getNodeIds();

        final LinkedHashSet<NodeOrderValueResolver.NodeIdOrderValue> orderedNodeIds = NodeOrderValueResolver.resolve( childNodeIds );

        for ( final NodeOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : orderedNodeIds )
        {
            // TODO: Bulk?

            final Node node = doGetNode( nodeIdOrderValue.getNodeId(), false );

            final Node editedNode = Node.editNode( node ).manualOrderValue( (long) nodeIdOrderValue.getManualOrderValue() ).build();

            doStoreNode( editedNode );
        }
    }

    public static final class Builder
        extends AbstractFindNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private ChildOrder childOrdrer;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrdrer = childOrder;
            return this;
        }

        public SetNodeChildOrderCommand build()
        {
            return new SetNodeChildOrderCommand( this );
        }
    }
}
