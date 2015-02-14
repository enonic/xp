package com.enonic.xp.core.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.form.BreaksRequiredContractException;

final class Phone
    extends InputType
{
    Phone()
    {

    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newString( value );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}

