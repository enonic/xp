package com.enonic.xp.form.inputtype;

import com.google.common.base.Joiner;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueType;

public final class InputTypeValidationException
    extends RuntimeException
{
    public InputTypeValidationException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }

    public static InputTypeValidationException invalidType( final Property property, final ValueType... expectedTypes )
    {
        final String strList = Joiner.on( "," ).join( expectedTypes );
        throw new InputTypeValidationException( "Invalid type in [%s]: [%s] instead of |%s]", property, property.getType(), strList );
    }

    public static InputTypeValidationException invalidValue( final Property property, final String message )
    {
        return new InputTypeValidationException( "Invalid value in [%s]: %s", property, message );
    }
}
