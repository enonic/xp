package com.enonic.wem.query.expr;

import com.enonic.wem.query.OrderSpec;
import com.enonic.wem.query.order.GeoDistanceOrder;

public final class GeoDistanceOrderFieldExpr
    extends FieldExpr
{
    private final FieldExpr field;

    private final ValueExpr location;

    private final ValueExpr unit;

    public GeoDistanceOrderFieldExpr( final FieldExpr field, final ValueExpr location, final ValueExpr unit )
    {
        super( field.getValue() );

        this.field = field;
        this.location = location;
        this.unit = unit;
    }

    public OrderSpec toOrder( final OrderSpec.Direction direction )
    {
        return new GeoDistanceOrder( direction, this, location, unit );
    }

    public FieldExpr getField()
    {
        return field;
    }
}
