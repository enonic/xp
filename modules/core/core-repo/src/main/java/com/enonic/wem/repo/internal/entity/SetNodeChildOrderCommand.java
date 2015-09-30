package com.enonic.wem.repo.internal.entity;

import java.util.LinkedList;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.search.SearchService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.security.acl.Permission;

public class SetNodeChildOrderCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private SetNodeChildOrderCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.childOrder = builder.childOrder;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node parentNode = doGetById( nodeId );

        checkContextUserPermissionOrAdmin( parentNode );

        final boolean newOrderingIsManual = childOrder.isManualOrder();
        final boolean childrenAreUnordered = !parentNode.getChildOrder().isManualOrder();
        final boolean childrenMustBeOrdered = newOrderingIsManual && childrenAreUnordered;

        if ( childrenMustBeOrdered )
        {
            orderChildNodes( parentNode );
        }

        final Node editedNode = Node.create( parentNode ).childOrder( childOrder ).build();

        StoreNodeCommand.create( this ).
            node( editedNode ).
            build().
            execute();

        return doGetById( editedNode.id() );
    }

    private void checkContextUserPermissionOrAdmin( final Node parentNode )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, parentNode );
    }

    private void orderChildNodes( final Node parentNode )
    {
        final NodeQueryResult childNodeResult = searchService.search( NodeQuery.create().
            parent( parentNode.path() ).
            query( new QueryExpr( parentNode.getChildOrder().getOrderExpressions() ) ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        final LinkedList<NodeId> childNodeIds = childNodeResult.getNodeQueryResultSet().getNodeIds();

        final LinkedList<NodeManualOrderValueResolver.NodeIdOrderValue> orderedNodeIds =
            NodeManualOrderValueResolver.resolve( childNodeIds );

        for ( final NodeManualOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : orderedNodeIds )
        {
            // TODO: Bulk?

            final Node node = doGetById( nodeIdOrderValue.getNodeId() );

            final Node editedNode = Node.create( node ).manualOrderValue( nodeIdOrderValue.getManualOrderValue() ).build();

            StoreNodeCommand.create( this ).
                node( editedNode ).
                build().
                execute();
        }
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private ChildOrder childOrder;

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

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId );
        }

        public SetNodeChildOrderCommand build()
        {
            return new SetNodeChildOrderCommand( this );
        }
    }
}
