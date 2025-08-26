package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class ReorderChildNodesCommand
    extends AbstractNodeCommand
{
    private final ReorderChildNodesParams params;

    private ReorderChildNodesCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ReorderChildNodesResult execute()
    {
        final ReorderChildNodesResult.Builder result = ReorderChildNodesResult.create();

        final List<Node> parents = new ArrayList<>();

        for ( final ReorderChildNodeParams reorderChildNodeParams : params )
        {
            refresh( RefreshMode.SEARCH );

            final Node nodeToMove = doGetById( reorderChildNodeParams.getNodeId() );

            final Long toMoveBeforeValue;
            if ( reorderChildNodeParams.getMoveBefore() == null )
            {
                toMoveBeforeValue = null;
            }
            else
            {
                final Node nodeToMoveBefore = doGetById( reorderChildNodeParams.getMoveBefore() );
                toMoveBeforeValue = nodeToMoveBefore.getManualOrderValue();
                if ( toMoveBeforeValue == null )
                {
                    throw new IllegalArgumentException( "Node with id [" + nodeToMoveBefore.id() + "] missing manual order value" );
                }
            }

            final Node parentNode =
                parents.stream().filter( node -> node.path().equals( nodeToMove.parentPath() ) ).findAny().orElseGet( () -> {
                    final Node node = doGetByPath( nodeToMove.parentPath() );
                    if ( !node.getChildOrder().isManualOrder() )
                    {
                        throw new IllegalArgumentException(
                            "Cannot manually order node with id [" + nodeToMove.id() + "] since manual child order not enabled on parent" );
                    }
                    return node;
                } );

            final NodePath parentNodePath = parentNode.path();
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
            final Node reorderedNode = doUpdateNodeOrderValue( nodeToMove, newOrderValue );

            result.addNodeId( reorderedNode.id() );

            if ( parents.stream().noneMatch( parent -> parent.id().equals( parentNode.id() ) ) )
            {
                parents.add( parentNode );
            }
        }

        parents.forEach( this::processParent );
        parents.forEach( result::addParentNode );

        refresh( params.getRefresh() );

        return result.build();
    }

    private void processParent( final Node parentNode )
    {
        final PropertyTree processedData = params.getProcessor().process( parentNode.data().copy(), parentNode.path() );
        if ( !processedData.equals( parentNode.data() ) )
        {
            final Node editedNode = Node.create( parentNode ).data( processedData ).timestamp( Instant.now( CLOCK ) ).build();
            this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode ), InternalContext.from( ContextAccessor.current() ) );
        }
    }

    private Long getManualOrderValue( final NodePath parentNodePath, final long nodeAfterOrderValue )
    {
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

    private Node doUpdateNodeOrderValue( Node nodeToMove, final long newOrderValue )
    {
        final Node updatedNode = Node.create( nodeToMove ).timestamp( Instant.now( CLOCK ) ).manualOrderValue( newOrderValue ).build();
        return this.nodeStorageService.store( updatedNode, InternalContext.from( ContextAccessor.current() ) ).node();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private ReorderChildNodesParams params;

        private Builder()
        {
        }

        public Builder params( final ReorderChildNodesParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public ReorderChildNodesCommand build()
        {
            this.validate();
            return new ReorderChildNodesCommand( this );
        }
    }
}
