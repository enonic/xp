package com.enonic.xp.query.expr;

import java.util.Collection;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public class OrderExpressions
    extends AbstractImmutableEntityList<OrderExpr>
{
    private OrderExpressions( final ImmutableList<OrderExpr> list )
    {
        super( list );
    }

    public static OrderExpressions empty()
    {
        return new OrderExpressions( ImmutableList.<OrderExpr>of() );
    }

    public static OrderExpressions from( final OrderExpr... orderExprs )
    {
        return new OrderExpressions( ImmutableList.copyOf( orderExprs ) );
    }

    public static OrderExpressions from( final Collection<OrderExpr> orderExprs )
    {
        return new OrderExpressions( ImmutableList.copyOf( orderExprs ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final List<OrderExpr> orderExprs = Lists.newLinkedList();

        public Builder add( final OrderExpr orderExpr )
        {
            this.orderExprs.add( orderExpr );
            return this;
        }

        public OrderExpressions build()
        {
            return new OrderExpressions( ImmutableList.copyOf( this.orderExprs ) );
        }

    }

}
