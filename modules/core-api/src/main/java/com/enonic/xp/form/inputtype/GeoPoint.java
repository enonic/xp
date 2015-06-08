package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class GeoPoint
    extends InputType
{
    GeoPoint()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final com.enonic.xp.util.GeoPoint value = property.getGeoPoint();
        if ( value == null )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.GEO_POINT.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.GEO_POINT );
        }
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newGeoPoint( com.enonic.xp.util.GeoPoint.from( value ) );
    }
}
