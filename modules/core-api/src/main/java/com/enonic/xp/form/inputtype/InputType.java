package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.form.Occurrences;

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

    public abstract void checkBreaksRequiredContract( final Property property );

    public abstract void checkTypeValidity( final Property property );

    @Override
    public String toString()
    {
        return this.name.toString();
    }

    public void validateOccurrences( final Occurrences occurrences )
    {
        // Default: nothing
    }

    public abstract Value createPropertyValue( final String value, final InputTypeConfig config );

    protected final void validateType( final Property property, final ValueType type )
    {
        if ( property.getType() != type )
        {
            throw new InvalidTypeException( property, ValueTypes.STRING );
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

    public InputTypeConfig parseConfig( ApplicationKey app, Element elem )
    {
        return null;
    }

    public ObjectNode serializeConfig( InputTypeConfig config )
    {
        return null;
    }

    public void checkValidity( InputTypeConfig config, Property property )
    {
    }
}
