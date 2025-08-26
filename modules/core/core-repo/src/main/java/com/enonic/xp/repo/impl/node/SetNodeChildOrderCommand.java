package com.enonic.xp.repo.impl.node;

import java.time.Instant;
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

    private final ChildOrder manualOrderBase;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private SetNodeChildOrderCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.childOrder = builder.childOrder;
        this.manualOrderBase = builder.manualOrderBase;
        this.processor = builder.processor;
        this.refresh = builder.refresh;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node node = doGetById( nodeId );

        checkContextUserPermissionOrAdmin( node );

        if ( childOrder.isManualOrder() && !node.getChildOrder().isManualOrder() )
        {
            orderChildNodes( node );
        }

        final Node editedNode = Node.create( node ).
            childOrder( childOrder ).data( processor.process( node.data(), node.path() ) ).
            timestamp( Instant.now( CLOCK ) ).
            build();

        final Node updatedNode =
            this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode ), InternalContext.from( ContextAccessor.current() ) )
                .node();
        refresh( refresh );
        return updatedNode;
    }

    private void checkContextUserPermissionOrAdmin( final Node parentNode )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, parentNode );
    }

    private void orderChildNodes( final Node parentNode )
    {
        refresh( RefreshMode.SEARCH );
        final NodeQuery query = NodeQuery.create()
            .parent( parentNode.path() )
            .query( new QueryExpr( Objects.requireNonNullElse( manualOrderBase, parentNode.getChildOrder() ).getOrderExpressions() ) )
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .batchSize( BATCH_SIZE )
            .build();
        final SearchResult result = nodeSearchService.query( query, SingleRepoSearchSource.from( ContextAccessor.current() ) );

        final NodeIds childNodeIds = NodeIds.from( result.getIds() );

        final NodeManualOrderValueResolver resolver = new NodeManualOrderValueResolver();
        for ( final NodeId nodeId : childNodeIds )
        {
            final Node node = doGetById( nodeId );

            final Node editedNode = Node.create( node ).
                manualOrderValue( resolver.getAsLong() ).
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

        private ChildOrder manualOrderBase;

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

        public Builder manualOrderBase( final ChildOrder childOrder )
        {
            this.manualOrderBase = childOrder;
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
