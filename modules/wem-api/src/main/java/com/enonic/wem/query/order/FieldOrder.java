package com.enonic.wem.query.order;

import com.google.common.base.Preconditions;

import com.enonic.wem.query.OrderSpec;
import com.enonic.wem.query.expr.FieldExpr;

public final class FieldOrder
    extends OrderSpec
{
    private final FieldExpr field;

    public FieldOrder( final Direction direction, final FieldExpr field )
    {
        super( direction );
        Preconditions.checkNotNull( field, "Field cannot be null" );
        this.field = field;
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    @Override
    public String toString()
    {
        return this.field.toString() + " " + getDirection().toString();
    }

}
