package com.enonic.wem.core.entity;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.core.entity.index.IndexPaths;

public class ChildOrder
{
    public static final OrderExpr DEFAULT_CHILD_ORDER_EXPRESSION =
        new FieldOrderExpr( FieldExpr.from( IndexPaths.MODIFIED_TIME_KEY ), OrderExpr.Direction.DESC );

    private final ImmutableSet<OrderExpr> childOrderExpressions;

    private ChildOrder( Builder builder )
    {
        childOrderExpressions = ImmutableSet.copyOf( builder.childOrderExpressions );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Set<OrderExpr> childOrderExpressions = Sets.newLinkedHashSet();

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
            if ( this.childOrderExpressions.isEmpty() )
            {
                this.childOrderExpressions.add( DEFAULT_CHILD_ORDER_EXPRESSION );
            }
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
