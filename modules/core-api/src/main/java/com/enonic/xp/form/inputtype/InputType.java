package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.InvalidTypeException;

@Beta
public abstract class InputType
{
    private final InputTypeName name;

    protected InputType( final InputTypeName name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name.toString();
    }

    public void checkBreaksRequiredContract( final Property property )
    {
    }

    @Override
    public String toString()
    {
        return this.name.toString();
    }

    public abstract Value createPropertyValue( final String value, final InputTypeConfig config );

    protected final void validateType( final Property property, final ValueType type )
    {
        if ( property.getType() != type )
        {
            throw new InvalidTypeException( property, type );
        }
    }

    protected final void validateNotBlank( final Property property )
    {
        final String stringValue = property.getString();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new InputValidationException( property, this );
        }
    }

    protected final void validateNotNull( final Property property, final Object value )
    {
        if ( value == null )
        {
            throw new InputValidationException( property, this );
        }
    }

    public abstract void checkValidity( InputTypeConfig config, Property property );
}
