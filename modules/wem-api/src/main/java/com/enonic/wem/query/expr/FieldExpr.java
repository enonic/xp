package com.enonic.wem.query.expr;

import com.enonic.wem.query.OrderSpec;
import com.enonic.wem.query.order.FieldOrder;

public class FieldExpr
    extends ValueExpr<String>
{
    public FieldExpr( final String value )
    {
        super( value );
    }

    @Override
    public String toString()
    {
        return getValue();
    }

    public OrderSpec toOrder( final OrderSpec.Direction direction )
    {
        return new FieldOrder( direction, this );
    }

    @Override
    public String getValueAsString()
    {
        return toString();
    }
}
