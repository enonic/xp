package com.enonic.wem.query.ast;

public final class FieldOrderExpr
    extends OrderExpr
{
    private final FieldExpr field;

    public FieldOrderExpr( final FieldExpr field, final Direction direction )
    {
        super( direction );
        this.field = field;
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    @Override
    public String toString()
    {
        return this.field + " " + getDirection();
    }
}
