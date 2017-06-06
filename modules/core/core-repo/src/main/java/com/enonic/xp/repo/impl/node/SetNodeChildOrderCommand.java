package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.acl.Permission;

public class SetNodeChildOrderCommand
    extends AbstractNodeCommand
{

    private static final int BATCH_SIZE = 10_000;

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

        final Node editedNode = Node.create( parentNode ).
            childOrder( childOrder ).
            timestamp( Instant.now() ).
            build();

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
        final NodeQueryResult childNodeResult = nodeSearchService.query( NodeQuery.create().
            parent( parentNode.path() ).
            query( new QueryExpr( parentNode.getChildOrder().getOrderExpressions() ) ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            batchSize( BATCH_SIZE ).
            searchMode( SearchMode.SEARCH ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        final List<NodeId> childNodeIds = childNodeResult.getNodeQueryResultSet().getNodeIds();

        final List<NodeManualOrderValueResolver.NodeIdOrderValue> orderedNodeIds = NodeManualOrderValueResolver.resolve( childNodeIds );

        for ( final NodeManualOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : orderedNodeIds )
        {
            final Node node = doGetById( nodeIdOrderValue.getNodeId() );

            final Node editedNode = Node.create( node ).
                manualOrderValue( nodeIdOrderValue.getManualOrderValue() ).
                timestamp( Instant.now() ).
                build();

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
