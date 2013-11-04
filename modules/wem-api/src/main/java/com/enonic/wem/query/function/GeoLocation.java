package com.enonic.wem.query.function;

import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.ValueExpr;

public final class GeoLocation
    extends ValueExpr<ValueExpr>
{
    public final static String NAME = "geoLocation";

    public GeoLocation( final ValueExpr field )
    {
        super( field );
    }

    @Override
    public String getValueAsString()
    {
        return "geoLocation(" +getValue()+ ")";
    }

    @Override
    public String toString()
    {
        return getValueAsString();
    }
}
