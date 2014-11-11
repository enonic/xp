package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;

public class MoveChildNodeCommand
    extends AbstractNodeCommand
{
    private final Node nodeToMove;

    private final Node parentNode;

    private final Node nodeToMoveBefore;

    private MoveChildNodeCommand( final Builder builder )
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

        return queryService.find( query, IndexContext.from( ContextAccessor.current() ) );
    }

    private Node doUpdateNodeOrderValue( final Long newOrderValue )
    {
        final Node updatedNode = Node.editNode( nodeToMove ).
            manualOrderValue( newOrderValue ).
            build();

        doStoreNode( updatedNode );

        return updatedNode;
    }

    private NodeQuery createFirstNodeBeforeInsertQuery( final Long nodeAfterOrderValue )
    {
        final CompareExpr orderGreaterThanNodeToMoveBefore =
            CompareExpr.gt( FieldExpr.from( IndexPaths.MANUAL_ORDER_VALUE_KEY ), ValueExpr.number( nodeAfterOrderValue ) );

        final FieldOrderExpr orderManuallyDesc = FieldOrderExpr.create( IndexPaths.MANUAL_ORDER_VALUE_KEY, OrderExpr.Direction.ASC );

        return NodeQuery.create().query( QueryExpr.from( orderGreaterThanNodeToMoveBefore, orderManuallyDesc ) ).
            size( 1 ).
            build();
    }

    private Long resolveInsertInbetweenOrderValue( final Long nodeAfterOrderValue, final NodeQueryResult result )
    {
        final NodeId nodeBeforeInsertId = result.getNodeQueryResultSet().first();
        final Node nodeBeforeInsert = doGetById( nodeBeforeInsertId, false );

        return ( nodeAfterOrderValue + nodeBeforeInsert.getManualOrderValue() ) / 2;
    }

    private Long resolveInsertFirstOrderValue( final Long nodeAfterOrderValue )
    {
        return nodeAfterOrderValue + NodeOrderValueResolver.ORDER_SPACE;
    }

    private Long resolveInsertLastOrderValue( final NodeQueryResult result )
    {
        final NodeId lastNodeId = result.getNodeQueryResultSet().first();
        final Node lastNode = doGetById( lastNodeId, false );

        return lastNode.getManualOrderValue() - NodeOrderValueResolver.ORDER_SPACE;
    }

    private Long resoleOnlyNodeOrderValue()
    {
        return NodeOrderValueResolver.START_ORDER_VALUE;
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

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( parentNode );
            Preconditions.checkNotNull( nodeToMove );
        }

        public MoveChildNodeCommand build()
        {
            return new MoveChildNodeCommand( this );
        }
    }
}
