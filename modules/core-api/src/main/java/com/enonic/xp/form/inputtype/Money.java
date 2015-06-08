package com.enonic.xp.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class Money
    extends InputType
{
    Money()
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
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.DOUBLE.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.DOUBLE );
        }
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newDouble( ValueTypes.DOUBLE.convert( value ) );
    }
}

