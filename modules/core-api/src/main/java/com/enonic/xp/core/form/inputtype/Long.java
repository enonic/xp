package com.enonic.xp.core.form.inputtype;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.data.ValueTypes;
import com.enonic.xp.core.form.BreaksRequiredContractException;

final class Long
    extends InputType
{
    Long()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final java.lang.Long value = property.getLong();
        if ( value == null )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newLong( ValueTypes.LONG.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
