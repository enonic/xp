package com.enonic.xp.index;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.parser.QueryParser;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class ChildOrder
{
    private static final OrderExpr DEFAULT_ORDER = FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC );

    private static final OrderExpr REVERSE_DEFAULT_ORDER = FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.ASC );

    private static final FieldOrderExpr MANUAL_ORDER = FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC );

    private static final FieldOrderExpr PATH_ASC = FieldOrderExpr.create( NodeIndexPath.PATH, OrderExpr.Direction.ASC );

    private static final FieldOrderExpr PATH_DESC = FieldOrderExpr.create( NodeIndexPath.PATH, OrderExpr.Direction.DESC );

    private static final FieldOrderExpr PUBLISH_ASC = FieldOrderExpr.create( ContentIndexPath.PUBLISH_FIRST, OrderExpr.Direction.ASC );

    private static final FieldOrderExpr PUBLISH_DESC = FieldOrderExpr.create( ContentIndexPath.PUBLISH_FIRST, OrderExpr.Direction.DESC );

    private static final FieldOrderExpr MANUAL_ORDER_REVERSE =
        FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC );

    private final OrderExpressions orderExpressions;

    private ChildOrder( final Builder builder )
    {
        this.orderExpressions = OrderExpressions.from( builder.orderExpressions );
    }

    public static ChildOrder manualOrder()
    {
        return ChildOrder.create().
            add( MANUAL_ORDER ).
            add( DEFAULT_ORDER ).
            build();
    }

    public static ChildOrder reverseManualOrder()
    {
        return ChildOrder.create().
            add( MANUAL_ORDER_REVERSE ).
            add( REVERSE_DEFAULT_ORDER ).
            build();
    }

    public static ChildOrder defaultOrder()
    {
        return ChildOrder.create().
            add( DEFAULT_ORDER ).
            build();
    }

    public static ChildOrder path()
    {
        return ChildOrder.create().
            add( PATH_ASC ).
            build();
    }

    public static ChildOrder reversePath()
    {
        return ChildOrder.create().
            add( PATH_DESC ).
            build();
    }

    public static ChildOrder publish()
    {
        return ChildOrder.create().
            add( PUBLISH_ASC ).
            build();
    }

    public static ChildOrder reversePublish()
    {
        return ChildOrder.create().
            add( PUBLISH_DESC ).
            build();
    }

    public static ChildOrder from( final String orderExpression )
    {
        if ( isNullOrEmpty( orderExpression ) )
        {
            return null;
        }
        final ChildOrder.Builder builder = ChildOrder.create();

        final List<OrderExpr> orderExprs = QueryParser.parseOrderExpressions( orderExpression );

        orderExprs.forEach( builder::add );

        return builder.build();
    }

    public boolean isManualOrder()
    {
        if ( this.orderExpressions.isEmpty() )
        {
            return false;
        }

        final OrderExpr orderExpr = this.orderExpressions.iterator().next();

        if ( orderExpr instanceof FieldOrderExpr )
        {
            final FieldOrderExpr fieldOrderExpr = (FieldOrderExpr) orderExpr;
            return fieldOrderExpr.getField().getFieldPath().equalsIgnoreCase( MANUAL_ORDER.getField().getFieldPath() );
        }

        return false;
    }

    public boolean isEmpty()
    {
        return this.orderExpressions.isEmpty();
    }

    public OrderExpressions getOrderExpressions()
    {
        return orderExpressions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final Set<OrderExpr> orderExpressions = new LinkedHashSet<>();

        private Builder()
        {
        }

        public Builder add( final OrderExpr orderExpr )
        {
            this.orderExpressions.add( orderExpr );
            return this;
        }

        public ChildOrder build()
        {
            return new ChildOrder( this );
        }
    }

    @Override
    public String toString()
    {
        return this.orderExpressions.stream().
            map( OrderExpr::toString ).
            collect( Collectors.joining( ", " ) );
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof ChildOrder && orderExpressions.equals( ( (ChildOrder) o ).orderExpressions );
    }

    @Override
    public int hashCode()
    {
        return orderExpressions.hashCode();
    }
}
