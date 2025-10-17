package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class GeoPointType
    extends InputTypeBase
{
    public static final GeoPointType INSTANCE = new GeoPointType();

    private GeoPointType()
    {
        super( InputTypeName.GEO_POINT );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newGeoPoint( value.asGeoPoint() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.GEO_POINT );
    }
}
