package com.enonic.xp.query.expr;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class OrderExpressions
    extends AbstractImmutableEntityList<OrderExpr>
{
    private static final OrderExpressions EMPTY = new OrderExpressions( ImmutableList.of() );

    private OrderExpressions( final ImmutableList<OrderExpr> list )
    {
        super( list );
    }

    public static OrderExpressions empty()
    {
        return EMPTY;
    }

    public static OrderExpressions from( final OrderExpr... orderExprs )
    {
        return fromInternal( ImmutableList.copyOf( orderExprs ) );
    }

    public static OrderExpressions from( final Collection<OrderExpr> orderExprs )
    {
        return fromInternal( ImmutableList.copyOf( orderExprs ) );
    }

    public static Collector<OrderExpr, ?, OrderExpressions> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), OrderExpressions::fromInternal );
    }

    private static OrderExpressions fromInternal( final ImmutableList<OrderExpr> set )
    {
        return set.isEmpty() ? EMPTY : new OrderExpressions( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final ImmutableList.Builder<OrderExpr> orderExprs = ImmutableList.builder();

        public Builder add( final OrderExpr orderExpr )
        {
            this.orderExprs.add( orderExpr );
            return this;
        }

        public OrderExpressions build()
        {
            return fromInternal( orderExprs.build() );
        }

    }

}
