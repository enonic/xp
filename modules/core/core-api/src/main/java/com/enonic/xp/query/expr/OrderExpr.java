package com.enonic.xp.query.expr;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class OrderExpr
    implements Expression
{
    public enum Direction
    {
        ASC, DESC
    }

    private final Direction direction;

    public OrderExpr( final Direction direction )
    {
        this.direction = direction;
    }

    public final Direction getDirection()
    {
        return this.direction;
    }

    @Override
    public abstract String toString();

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

        final OrderExpr orderExpr = (OrderExpr) o;

        return direction == orderExpr.direction;
    }

    @Override
    public int hashCode()
    {
        return direction != null ? direction.hashCode() : 0;
    }
}
