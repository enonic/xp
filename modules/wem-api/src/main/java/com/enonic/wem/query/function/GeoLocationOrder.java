package com.enonic.wem.query.function;

import com.enonic.wem.query.DynamicOrder;
import com.enonic.wem.query.FieldExpr;
import com.enonic.wem.query.ValueExpr;

public final class GeoLocationOrder
    extends DynamicOrder
{
    public final static String NAME = "geoLocation";

    public GeoLocationOrder( final Direction direction, final FieldExpr field, final ValueExpr location, final ValueExpr unit )
    {
        super( direction, NAME, field, location, unit );
    }
}
