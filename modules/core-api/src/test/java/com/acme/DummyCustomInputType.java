package com.acme;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.form.inputtype.InputTypeConfig;
import com.enonic.xp.form.inputtype.InputTypeExtension;

public class DummyCustomInputType
    extends InputTypeExtension
{

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.STRING.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.STRING );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return null;
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }
}
