package com.enonic.xp.inputtype;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.ValueType;

public final class InputTypeValidationException
    extends RuntimeException
{
    private final PropertyPath propertyPath;

    public InputTypeValidationException( final String message )
    {
        super( message );
        this.propertyPath = null;
    }

    private InputTypeValidationException( final String message, final PropertyPath propertyPath )
    {
        super( message );
        this.propertyPath = propertyPath;
    }

    public PropertyPath getPropertyPath()
    {
        return propertyPath;
    }

    public static InputTypeValidationException invalidType( final Property property, final ValueType... expectedTypes )
    {
        final String strList = Arrays.stream( expectedTypes ).map( Objects::toString ).
            collect( Collectors.joining( "," ) );
        throw new InputTypeValidationException(
            String.format( "Invalid type in [%s]: [%s] instead of |%s]", property, property.getType(), strList ), property.getPath() );
    }

    public static InputTypeValidationException invalidValue( final Property property, final String message )
    {
        return new InputTypeValidationException( String.format( "Invalid value in [%s]: %s", property, message ), property.getPath() );
    }
}
