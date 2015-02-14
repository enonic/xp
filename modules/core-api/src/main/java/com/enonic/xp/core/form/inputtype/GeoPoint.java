package com.enonic.xp.core.form.inputtype;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.form.BreaksRequiredContractException;

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
        final com.enonic.xp.core.util.GeoPoint value = property.getGeoPoint();
        if ( value == null )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newGeoPoint( com.enonic.xp.core.util.GeoPoint.from( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
