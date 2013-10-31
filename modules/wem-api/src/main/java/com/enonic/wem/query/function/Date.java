package com.enonic.wem.query.function;

import com.enonic.wem.query.DynamicConstraint;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.ValueExpr;

public final class Date
    extends ValueExpr<ValueExpr>
{
    public final static String NAME = "date";

    public Date( final ValueExpr field )
    {
        super( field );
    }

    @Override
    public String getValueAsString()
    {
        return "date(" +getValue()+ ")";
    }

    @Override
    public String toString()
    {
        return getValueAsString();
    }
}
