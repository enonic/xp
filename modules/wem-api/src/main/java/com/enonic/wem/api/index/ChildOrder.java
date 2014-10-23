package com.enonic.wem.api.index;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.parser.QueryParser;

public class ChildOrder
{
    private static final OrderExpr DEFAULT_ORDER = FieldOrderExpr.create( IndexPaths.MODIFIED_TIME_KEY, OrderExpr.Direction.DESC );

    private static final FieldOrderExpr MANUAL_ORDER = FieldOrderExpr.create( IndexPaths.MANUAL_ORDER_VALUE_KEY, OrderExpr.Direction.DESC );

    private final ImmutableSet<OrderExpr> childOrderExpressions;

    private ChildOrder( final Builder builder )
    {
        childOrderExpressions = ImmutableSet.copyOf( builder.childOrderExpressions );
    }

    public static ChildOrder manualOrder()
    {
        return ChildOrder.create().
            add( MANUAL_ORDER ).
            build();
    }

    public static ChildOrder defaultOrder()
    {
        return ChildOrder.create().
            add( DEFAULT_ORDER ).
            build();
    }

    public static ChildOrder from( final String orderExpression )
    {
        final ChildOrder.Builder builder = ChildOrder.create();

        if ( Strings.isNullOrEmpty( orderExpression ) )
        {
            return builder.build();
        }

        final List<OrderExpr> orderExprs = QueryParser.parseOrderExpressions( orderExpression );

        orderExprs.forEach( builder::add );

        return builder.build();
    }

    public boolean isManualOrder()
    {
        if ( this.childOrderExpressions.isEmpty() )
        {
            return false;
        }

        final OrderExpr orderExpr = this.childOrderExpressions.iterator().next();

        if ( orderExpr instanceof FieldOrderExpr )
        {
            final FieldOrderExpr fieldOrderExpr = (FieldOrderExpr) orderExpr;
            return fieldOrderExpr.getField().getName().equalsIgnoreCase( MANUAL_ORDER.getField().getName() );
        }

        return false;
    }

    public boolean isEmpty()
    {
        return this.childOrderExpressions.isEmpty();
    }

    public ImmutableSet<OrderExpr> getChildOrderExpressions()
    {
        return childOrderExpressions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final Set<OrderExpr> childOrderExpressions = Sets.newLinkedHashSet();

        private Builder()
        {
        }

        public Builder add( final OrderExpr childOrderExpr )
        {
            this.childOrderExpressions.add( childOrderExpr );
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
        return this.childOrderExpressions.stream().
            map( OrderExpr::toString ).
            collect( Collectors.joining( ", " ) );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ChildOrder that = (ChildOrder) o;

        if ( childOrderExpressions != null
            ? !childOrderExpressions.equals( that.childOrderExpressions )
            : that.childOrderExpressions != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return childOrderExpressions != null ? childOrderExpressions.hashCode() : 0;
    }
}
