package com.enonic.wem.query.function;

import com.enonic.wem.query.DynamicConstraint;
import com.enonic.wem.query.FieldExpr;
import com.enonic.wem.query.ValueExpr;

public final class GeoDistance
    extends DynamicConstraint
{
    public final static String NAME = "geoDistance";

    public GeoDistance( final FieldExpr field, final ValueExpr location, final ValueExpr distance )
    {
        super( NAME, field, location, distance );
    }
}
