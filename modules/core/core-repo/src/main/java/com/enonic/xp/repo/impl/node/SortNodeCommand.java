package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Ordering;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.node.SortNodeResult;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class SortNodeCommand
    extends AbstractNodeCommand
{
    private static final int BATCH_SIZE = 10_000;

    private final SetNodeChildOrderParams params;

    private SortNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public SortNodeResult execute()
    {
        final Node node = doGetById( params.getNodeId() );

        checkContextUserPermissionOrAdmin( node );

        final SortNodeResult.Builder result = SortNodeResult.create();

        if ( params.getChildOrder().isManualOrder() )
        {
            if ( !node.getChildOrder().isManualOrder() )
            {
                orderChildNodes( node.path(), Objects.requireNonNullElse( params.getManualOrderBase(), node.getChildOrder() ),
                                 params.getReorderChildNodes(), result );
            }
            else
            {
                reoderChildNoes( node.path(), params.getReorderChildNodes(), result );
            }
        }

        final PropertyTree processedData = params.getProcessor().process( node.data(), node.path() );

        if ( !processedData.equals( node.data() ) || !Objects.equals( params.getChildOrder(), node.getChildOrder() ) )
        {
            final Node editedNode =
                Node.create( node ).childOrder( params.getChildOrder() ).data( processedData ).timestamp( Instant.now( CLOCK ) ).build();
            Node updatedNode =
                this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode ), InternalContext.from( ContextAccessor.current() ) )
                    .node();
            result.node( updatedNode );
        }
        else
        {
            result.node( node );
        }
        refresh( params.getRefresh() );
        return result.build();
    }

    private void checkContextUserPermissionOrAdmin( final Node parentNode )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, parentNode );
    }

    private void orderChildNodes( final NodePath parentNodePath, ChildOrder baseOrder, final List<ReorderChildNodeParams> reorderChildNodes,
                                  final SortNodeResult.Builder result )
    {
        refresh( RefreshMode.SEARCH );
        final NodeQuery query = NodeQuery.create()
            .parent( parentNodePath )
            .query( new QueryExpr( baseOrder.getOrderExpressions() ) )
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .batchSize( BATCH_SIZE )
            .build();

        final List<NodeId> childNodeIds = nodeSearchService.query( query, SingleRepoSearchSource.from( ContextAccessor.current() ) )
            .getIds()
            .stream()
            .distinct()
            .map( NodeId::from )
            .collect( Collectors.toCollection( ArrayList::new ) );

        for ( ReorderChildNodeParams param : reorderChildNodes )
        {
            final NodeId moveBefore = param.getMoveBefore();
            if ( childNodeIds.remove( param.getNodeId() ) )
            {
                if ( moveBefore == null )
                {
                    childNodeIds.add( param.getNodeId() );
                }
                else
                {
                    int index = childNodeIds.indexOf( moveBefore );
                    if ( index >= 0 )
                    {
                        childNodeIds.add( index, param.getNodeId() );
                    }
                    else
                    {
                        childNodeIds.add( param.getNodeId() );
                    }
                }
            }
        }

        final NodeManualOrderValueResolver resolver = new NodeManualOrderValueResolver();
        for ( final NodeId nodeId : childNodeIds )
        {
            final Node node = doGetById( nodeId );
            final Node updatedNode = Node.create( node ).manualOrderValue( resolver.getAsLong() ).timestamp( Instant.now( CLOCK ) ).build();
            final Node storedNode = this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode ),
                                                                   InternalContext.from( ContextAccessor.current() ) ).node();
            result.node( storedNode );
        }
    }

    private void reoderChildNoes( final NodePath parentNodePath, final List<ReorderChildNodeParams> reorderChildNodes,
                                  final SortNodeResult.Builder result )
    {
        for ( final ReorderChildNodeParams reorderChildNodeParams : reorderChildNodes )
        {
            final Long toMoveBeforeValue;
            if ( reorderChildNodeParams.getMoveBefore() == null )
            {
                toMoveBeforeValue = null;
            }
            else
            {
                final Node nodeToMoveBefore = doGetById( reorderChildNodeParams.getMoveBefore() );
                if ( !nodeToMoveBefore.parentPath().equals( parentNodePath ) )
                {
                    throw new IllegalArgumentException( "reordered nodes must be children of " + parentNodePath );
                }
                toMoveBeforeValue = nodeToMoveBefore.getManualOrderValue();
            }

            final long newOrderValue;
            if ( toMoveBeforeValue == null )
            {
                final Long nodeManualOrderValue = getManualOrderValue( parentNodePath, Long.MIN_VALUE );

                newOrderValue = nodeManualOrderValue == null
                    ? NodeManualOrderValueResolver.first()
                    : NodeManualOrderValueResolver.after( nodeManualOrderValue );
            }
            else
            {
                final Long nodeManualOrderValue = getManualOrderValue( parentNodePath, toMoveBeforeValue );

                newOrderValue = nodeManualOrderValue == null
                    ? NodeManualOrderValueResolver.before( toMoveBeforeValue )
                    : NodeManualOrderValueResolver.between( toMoveBeforeValue, nodeManualOrderValue );
            }

            final Node node = doGetById( reorderChildNodeParams.getNodeId() );
            if ( !node.parentPath().equals( parentNodePath ) )
            {
                throw new IllegalArgumentException( "reordered nodes must be children of " + parentNodePath );
            }

            final Node updatedNode = Node.create( node ).timestamp( Instant.now( CLOCK ) ).manualOrderValue( newOrderValue ).build();
            final Node storedNode = this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode ),
                                                                   InternalContext.from( ContextAccessor.current() ) ).node();

            result.addReorderedNode( storedNode );
        }
    }

    private Long getManualOrderValue( final NodePath parentNodePath, final long nodeAfterOrderValue )
    {
        refresh( RefreshMode.SEARCH );
        final NodeQuery query = NodeQuery.create()
            .parent( parentNodePath )
            .query( QueryExpr.from(
                CompareExpr.gt( FieldExpr.from( NodeIndexPath.MANUAL_ORDER_VALUE ), ValueExpr.number( nodeAfterOrderValue ) ),
                FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ) )
            .size( 1 )
            .build();
        final SearchResult searchResult = nodeSearchService.query( query, SingleRepoSearchSource.from( ContextAccessor.current() ) );
        if ( searchResult.isEmpty() )
        {
            return null;
        }

        final Node node = doGetById( NodeId.from( searchResult.getHits().getFirst().getId() ) );
        final Long manualOrderValue = node.getManualOrderValue();
        if ( manualOrderValue == null )
        {
            throw new IllegalArgumentException( "Node with id [" + node.id() + "] missing manual order value" );
        }

        return manualOrderValue;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private SetNodeChildOrderParams params;

        private Builder()
        {
        }

        public Builder params( SetNodeChildOrderParams params )
        {
            this.params = params;
            return this;
        }


        @Override
        void validate()
        {
            super.validate();
        }

        public SortNodeCommand build()
        {
            validate();
            return new SortNodeCommand( this );
        }
    }
}
