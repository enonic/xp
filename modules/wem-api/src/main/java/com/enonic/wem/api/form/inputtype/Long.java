package com.enonic.wem.api.form.inputtype;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.Value;
import com.enonic.wem.api.data2.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

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
