package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

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

        final NodeQueryResult result = findLastNodeBeforeInsert( nodeAfterOrderValue );

        final Long newOrderValue;

        if ( result.getNodeQueryResultSet().isEmpty() )
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
        final NodeQueryResult result = findLastNodeBeforeInsert( Long.MIN_VALUE );

        final Long newOrderValue;

        if ( result.getNodeQueryResultSet().isEmpty() )
        {
            newOrderValue = resoleOnlyNodeOrderValue();
        }
        else
        {
            newOrderValue = resolveInsertLastOrderValue( result );
        }

        return doUpdateNodeOrderValue( newOrderValue );
    }

    private NodeQueryResult findLastNodeBeforeInsert( final Long nodeAfterOrderValue )
    {
        final NodeQuery query = createFirstNodeBeforeInsertQuery( nodeAfterOrderValue );

        return searchService.search( query, InternalContext.from( ContextAccessor.current() ) );
    }

    private Node doUpdateNodeOrderValue( final Long newOrderValue )
    {
        final Node updatedNode = Node.create( nodeToMove ).
            manualOrderValue( newOrderValue ).
            build();

        return StoreNodeCommand.create( this ).
            node( updatedNode ).
            build().
            execute();
    }

    private NodeQuery createFirstNodeBeforeInsertQuery( final Long nodeAfterOrderValue )
    {
        final CompareExpr orderGreaterThanNodeToMoveBefore =
            CompareExpr.gt( FieldExpr.from( NodeIndexPath.MANUAL_ORDER_VALUE ), ValueExpr.number( nodeAfterOrderValue ) );

        final CompareExpr parentPathEqualToParent =
            CompareExpr.eq( FieldExpr.from( NodeIndexPath.PARENT_PATH ), ValueExpr.string( parentNode.path().toString() ) );

        final LogicalExpr constraint = LogicalExpr.and( orderGreaterThanNodeToMoveBefore, parentPathEqualToParent );

        final FieldOrderExpr orderManuallyDesc = FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC );

        return NodeQuery.create().query( QueryExpr.from( constraint, orderManuallyDesc ) ).
            size( 1 ).
            build();
    }

    private Long resolveInsertInbetweenOrderValue( final Long nodeAfterOrderValue, final NodeQueryResult result )
    {
        final NodeId nodeBeforeInsertId = result.getNodeQueryResultSet().first();
        final Node nodeBeforeInsert = doGetById( nodeBeforeInsertId );

        return ( nodeAfterOrderValue + nodeBeforeInsert.getManualOrderValue() ) / 2;
    }

    private Long resolveInsertFirstOrderValue( final Long nodeAfterOrderValue )
    {
        return nodeAfterOrderValue + NodeManualOrderValueResolver.ORDER_SPACE;
    }

    private Long resolveInsertLastOrderValue( final NodeQueryResult result )
    {
        final NodeId lastNodeId = result.getNodeQueryResultSet().first();
        final Node lastNode = doGetById( lastNodeId );

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
