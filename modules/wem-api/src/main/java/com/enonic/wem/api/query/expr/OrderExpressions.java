package com.enonic.wem.api.query.expr;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class OrderExpressions
    extends AbstractImmutableEntitySet<OrderExpr>
{
    private OrderExpressions( final ImmutableSet<OrderExpr> set )
    {
        super( set );
    }

    public static OrderExpressions empty()
    {
        return new OrderExpressions( ImmutableSet.<OrderExpr>of() );
    }

    public static OrderExpressions from( final OrderExpr... orderExprs )
    {
        return new OrderExpressions( ImmutableSet.copyOf( orderExprs ) );
    }

    public static OrderExpressions from( final Collection<OrderExpr> orderExprs )
    {
        return new OrderExpressions( ImmutableSet.copyOf( orderExprs ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<OrderExpr> orderExprs = Sets.newLinkedHashSet();

        public Builder add( final OrderExpr orderExpr )
        {
            this.orderExprs.add( orderExpr );
            return this;
        }

        public OrderExpressions build()
        {
            return new OrderExpressions( ImmutableSet.copyOf( this.orderExprs ) );
        }

    }


}
