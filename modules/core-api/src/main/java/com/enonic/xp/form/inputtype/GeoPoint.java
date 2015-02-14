package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.BreaksRequiredContractException;

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
    public Value newValue( final String value )
    {
        return Value.newGeoPoint( com.enonic.xp.util.GeoPoint.from( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
