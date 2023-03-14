package com.enonic.xp.query.expr;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.IndexPath;

@PublicApi
public final class FieldOrderExpr
    extends OrderExpr
{
    private final FieldExpr field;

    public FieldOrderExpr( final FieldExpr field, final Direction direction )
    {
        super( direction );
        this.field = Objects.requireNonNull( field );
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    public static FieldOrderExpr create( final IndexPath indexPath, Direction direction )
    {
        return new FieldOrderExpr( FieldExpr.from( indexPath ), direction );
    }

    public static FieldOrderExpr create( final String indexPath, Direction direction )
    {
        return new FieldOrderExpr( FieldExpr.from( indexPath ), direction );
    }

    @Override
    public String toString()
    {
        return this.field + ( getDirection() != null ? " " + getDirection().name() : "" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof FieldOrderExpr ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final FieldOrderExpr that = (FieldOrderExpr) o;
        return field.equals( that.field );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), field );
    }
}
