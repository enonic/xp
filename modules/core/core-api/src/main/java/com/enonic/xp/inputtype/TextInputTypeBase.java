package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.util.GenericValue;

abstract class TextInputTypeBase
    extends InputTypeBase
{
    TextInputTypeBase( final InputTypeName name )
    {
        super( name );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateMaxLength( property, config );
    }

    private void validateMaxLength( final Property property, final GenericValue config )
    {
        final Integer maxLength = config.optional( "maxLength" ).map( GenericValue::asInteger ).orElse( null );

        final String propertyValue = property.getString();

        if ( maxLength != null && propertyValue != null )
        {
            validateValue( property, propertyValue.length() <= maxLength,
                           String.format( "Limit of %d character%s exceeded", maxLength, maxLength == 1 ? "" : "s" ) );
        }
    }
}
