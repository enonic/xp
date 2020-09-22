package com.enonic.xp.inputtype;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueType;

public final class InputTypeValidationException
    extends RuntimeException
{
    public InputTypeValidationException( final String message )
    {
        super( message );
    }

    @Deprecated
    public InputTypeValidationException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }

    public static InputTypeValidationException invalidType( final Property property, final ValueType... expectedTypes )
    {
        final String strList = Arrays.stream( expectedTypes ).map( Objects::toString ).
            collect( Collectors.joining( "," ) );
        throw new InputTypeValidationException(
            String.format( "Invalid type in [%s]: [%s] instead of |%s]", property, property.getType(), strList ) );
    }

    public static InputTypeValidationException invalidValue( final Property property, final String message )
    {
        return new InputTypeValidationException( String.format( "Invalid value in [%s]: %s", property, message ) );
    }
}
