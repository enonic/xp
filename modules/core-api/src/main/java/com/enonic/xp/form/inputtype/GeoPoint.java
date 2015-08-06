package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class GeoPoint
    extends InputType
{
    public GeoPoint()
    {
        super( InputTypeName.GEO_POINT, null, false );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotNull( property, property.getGeoPoint() );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.GEO_POINT );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newGeoPoint( com.enonic.xp.util.GeoPoint.from( value ) );
    }
}
