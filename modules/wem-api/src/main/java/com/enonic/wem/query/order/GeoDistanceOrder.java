package com.enonic.wem.query.order;

import com.enonic.wem.query.DynamicOrder;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.ValueExpr;

public final class GeoDistanceOrder
    extends DynamicOrder
{
    public final static String NAME = "GEODISTANCEORDER";

    public GeoDistanceOrder( final Direction direction, final FieldExpr field, final ValueExpr location, final ValueExpr unit )
    {
        super( direction, NAME, field, location, unit );
    }

}
