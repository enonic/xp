package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

/* temporary solution for custom:className */
@Beta
public class CustomInputType
    extends InputType
{
    private String className;

    public CustomInputType( String className )
    {
        this.className = className;
    }

    @Override
    public String getName()
    {
        return className;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
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
