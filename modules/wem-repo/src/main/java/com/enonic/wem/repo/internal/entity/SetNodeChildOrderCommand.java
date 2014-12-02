package com.enonic.wem.repo.internal.entity;

import java.util.LinkedHashSet;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentChildOrderUpdatedEvent;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;

public class SetNodeChildOrderCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private final EventPublisher eventPublisher;

    private SetNodeChildOrderCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.childOrder = builder.childOrder;
        this.eventPublisher = builder.eventPublisher;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node parentNode = doGetById( nodeId, false );

        final boolean newOrderingIsManual = childOrder.isManualOrder();
        final boolean childrenAreUnordered = !parentNode.getChildOrder().isManualOrder();
        final boolean childrenMustBeOrdered = newOrderingIsManual && childrenAreUnordered;

        if ( childrenMustBeOrdered )
        {
            orderChildNodes( parentNode );
        }

        final Node editedNode = Node.editNode( parentNode ).childOrder( childOrder ).build();

        doStoreNode( editedNode );

        eventPublisher.publish( new ContentChildOrderUpdatedEvent( ContentId.from( nodeId.toString() ) ) );

        return doGetById( editedNode.id(), false );
    }

    private void orderChildNodes( final Node parentNode )
    {
        final NodeQueryResult childNodeResult = queryService.find( NodeQuery.create().
            parent( parentNode.path() ).
            query( new QueryExpr( parentNode.getChildOrder().getOrderExpressions() ) ).
            build(), IndexContext.from( ContextAccessor.current() ) );

        final LinkedHashSet<NodeId> childNodeIds = childNodeResult.getNodeQueryResultSet().getNodeIds();

        final LinkedHashSet<NodeOrderValueResolver.NodeIdOrderValue> orderedNodeIds = NodeOrderValueResolver.resolve( childNodeIds );

        for ( final NodeOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : orderedNodeIds )
        {
            // TODO: Bulk?

            final Node node = doGetById( nodeIdOrderValue.getNodeId(), false );

            final Node editedNode = Node.editNode( node ).manualOrderValue( nodeIdOrderValue.getManualOrderValue() ).build();

            doStoreNode( editedNode );
        }
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private ChildOrder childOrder;

        private EventPublisher eventPublisher;

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
            this.childOrder = childOrder;
            return this;
        }

        public Builder eventPublisher( final EventPublisher eventPublisher )
        {
            this.eventPublisher = eventPublisher;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( nodeId );
            Preconditions.checkNotNull( childOrder );
            Preconditions.checkNotNull( eventPublisher );
        }

        public SetNodeChildOrderCommand build()
        {
            validate();
            return new SetNodeChildOrderCommand( this );
        }
    }
}
