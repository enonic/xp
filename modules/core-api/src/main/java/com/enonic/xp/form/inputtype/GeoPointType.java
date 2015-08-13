package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class GeoPointType
    extends InputTypeBase
{
    public final static GeoPointType INSTANCE = new GeoPointType();

    private GeoPointType()
    {
        super( InputTypeName.GEO_POINT );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return Value.newGeoPoint( com.enonic.xp.util.GeoPoint.from( value ) );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.GEO_POINT );
    }
}
