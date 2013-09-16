package com.enonic.wem.query;

import com.google.common.base.Preconditions;

public abstract class OrderSpec
    implements Expression
{
    public enum Direction
    {
        ASC,
        DESC
    }

    private final Direction direction;

    public OrderSpec( final Direction direction )
    {
        Preconditions.checkNotNull( direction, "Direction cannot be null" );
        this.direction = direction;
    }

    public final Direction getDirection()
    {
        return this.direction;
    }
}
