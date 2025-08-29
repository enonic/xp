package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class SetNodeChildOrderCommand
    extends AbstractNodeCommand
{

    private static final int BATCH_SIZE = 10_000;

    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private SetNodeChildOrderCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.childOrder = builder.childOrder;
        this.processor = builder.processor;
        this.refresh = builder.refresh;
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
            childOrder( childOrder ).data( processor.process( parentNode.data(), parentNode.path() ) ).
            timestamp( Instant.now( CLOCK ) ).
            build();

        final Node node =
            this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode ), InternalContext.from( ContextAccessor.current() ) )
                .node();
        refresh( refresh );
        return node;
    }

    private void checkContextUserPermissionOrAdmin( final Node parentNode )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, parentNode );
    }

    private void orderChildNodes( final Node parentNode )
    {
        refresh( RefreshMode.SEARCH );
        final SearchResult result = nodeSearchService.query( NodeQuery.create().
            parent( parentNode.path() ).
            query( new QueryExpr( parentNode.getChildOrder().getOrderExpressions() ) ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            batchSize( BATCH_SIZE ).
            build(), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        final NodeIds childNodeIds = NodeIds.from( result.getIds() );

        final List<NodeManualOrderValueResolver.NodeIdOrderValue> orderedNodeIds = NodeManualOrderValueResolver.resolve( childNodeIds );

        for ( final NodeManualOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : orderedNodeIds )
        {
            final Node node = doGetById( nodeIdOrderValue.getNodeId() );

            final Node editedNode = Node.create( node ).
                manualOrderValue( nodeIdOrderValue.getManualOrderValue() ).
                timestamp( Instant.now( CLOCK ) ).
                build();
            this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode ), InternalContext.from( ContextAccessor.current() ) );
        }
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private ChildOrder childOrder;

        private NodeDataProcessor processor = ( n, p ) -> n;

        private RefreshMode refresh;

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

        public Builder processor( final NodeDataProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( processor );
            Objects.requireNonNull( nodeId,  "nodeId is required" );
        }

        public SetNodeChildOrderCommand build()
        {
            validate();
            return new SetNodeChildOrderCommand( this );
        }

    }
}
