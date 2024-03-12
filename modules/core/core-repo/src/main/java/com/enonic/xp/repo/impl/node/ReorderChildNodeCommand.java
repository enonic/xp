package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class ReorderChildNodeCommand
    extends AbstractNodeCommand
{
    private final Node nodeToMove;

    private final Node parentNode;

    private final Node nodeToMoveBefore;

    private ReorderChildNodeCommand( final Builder builder )
    {
        super( builder );
        nodeToMove = builder.nodeToMove;
        parentNode = builder.parentNode;
        nodeToMoveBefore = builder.nodeToMoveBefore;
    }

    public Node execute()
    {
        if ( !parentNode.getChildOrder().isManualOrder() )
        {
            throw new IllegalArgumentException(
                "Cannot manually order child nodes of parent " + parentNode.path() + " since manual child order not enabled" );
        }

        if ( nodeToMoveBefore == null )
        {
            return doMoveLast();
        }
        else
        {
            return doMoveBefore();
        }
    }

    private Node doMoveBefore()
    {
        final Long nodeAfterOrderValue = nodeToMoveBefore.getManualOrderValue();

        if ( nodeAfterOrderValue == null )
        {
            throw new IllegalArgumentException( "Node with id [" + nodeToMoveBefore.id() + "] missing manual order value" );
        }

        final SearchResult result = findLastNodeBeforeInsert( nodeAfterOrderValue );

        final long newOrderValue;

        if ( result.isEmpty() )
        {
            newOrderValue = resolveInsertFirstOrderValue( nodeAfterOrderValue );
        }
        else
        {
            newOrderValue = resolveInsertInbetweenOrderValue( nodeAfterOrderValue, result );
        }

        return doUpdateNodeOrderValue( newOrderValue );
    }

    private Node doMoveLast()
    {
        final SearchResult result = findLastNodeBeforeInsert( Long.MIN_VALUE );

        final Long newOrderValue;

        if ( result.isEmpty() )
        {
            newOrderValue = resoleOnlyNodeOrderValue();
        }
        else
        {
            newOrderValue = resolveInsertLastOrderValue( result );
        }

        return doUpdateNodeOrderValue( newOrderValue );
    }

    private SearchResult findLastNodeBeforeInsert( final long nodeAfterOrderValue )
    {
        final CompareExpr orderGreaterThanNodeToMoveBefore =
            CompareExpr.gt( FieldExpr.from( NodeIndexPath.MANUAL_ORDER_VALUE ), ValueExpr.number( nodeAfterOrderValue ) );

        final FieldOrderExpr orderManuallyDesc = FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC );

        final NodeQuery query = NodeQuery.create()
            .parent( parentNode.path() )
            .query( QueryExpr.from( orderGreaterThanNodeToMoveBefore, orderManuallyDesc ) )
            .size( 1 )
            .build();

        return nodeSearchService.query( query, SingleRepoSearchSource.from( ContextAccessor.current() ) );
    }

    private Node doUpdateNodeOrderValue( final long newOrderValue )
    {
        final Node updatedNode = Node.create( nodeToMove ).timestamp( Instant.now( CLOCK ) ).manualOrderValue( newOrderValue ).build();

        return this.nodeStorageService.store( updatedNode, InternalContext.from( ContextAccessor.current() ) ).node();
    }

    private long resolveInsertInbetweenOrderValue( final Long nodeAfterOrderValue, final SearchResult result )
    {
        final NodeId nodeBeforeInsertId = NodeId.from( result.getHits().getFirst().getId() );
        final Node nodeBeforeInsert = doGetById( nodeBeforeInsertId );

        return ( nodeAfterOrderValue + nodeBeforeInsert.getManualOrderValue() ) / 2;
    }

    private long resolveInsertFirstOrderValue( final Long nodeAfterOrderValue )
    {
        return nodeAfterOrderValue + NodeManualOrderValueResolver.ORDER_SPACE;
    }

    private Long resolveInsertLastOrderValue( final SearchResult result )
    {
        final NodeId lastNodeId = NodeId.from( result.getHits().getFirst().getId() );
        final Node lastNode = doGetById( lastNodeId );

        if ( lastNode.getManualOrderValue() == null )
        {
            throw new IllegalArgumentException( "Node with id [" + lastNode.id() + "] missing manual order value" );
        }

        return lastNode.getManualOrderValue() - NodeManualOrderValueResolver.ORDER_SPACE;
    }

    private Long resoleOnlyNodeOrderValue()
    {
        return NodeManualOrderValueResolver.START_ORDER_VALUE;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Node nodeToMove;

        private Node parentNode;

        private Node nodeToMoveBefore;

        private Builder()
        {
        }

        public Builder nodeToMove( Node nodeToMove )
        {
            this.nodeToMove = nodeToMove;
            return this;
        }

        public Builder parentNode( Node parentNode )
        {
            this.parentNode = parentNode;
            return this;
        }

        public Builder nodeToMoveBefore( Node nodeToMoveBefore )
        {
            this.nodeToMoveBefore = nodeToMoveBefore;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( parentNode );
            Preconditions.checkNotNull( nodeToMove );
        }

        public ReorderChildNodeCommand build()
        {
            this.validate();
            return new ReorderChildNodeCommand( this );
        }
    }
}
