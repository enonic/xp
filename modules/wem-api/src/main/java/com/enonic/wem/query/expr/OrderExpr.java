package com.enonic.wem.query.expr;

public abstract class OrderExpr
    implements Expression
{
    public enum Direction
    {
        ASC,
        DESC
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
}
