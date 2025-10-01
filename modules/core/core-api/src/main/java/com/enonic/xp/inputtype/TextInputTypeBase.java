package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;

abstract class TextInputTypeBase
    extends InputTypeBase
{
    TextInputTypeBase( final InputTypeName name )
    {
        super( name );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateMaxLength( property, config );
    }

    private void validateMaxLength( final Property property, final InputTypeConfig config )
    {
        final Integer maxLength =
            config.getProperty( "maxLength" ).map( InputTypeProperty::getValue ).map( PropertyValue::asInteger ).orElse( null );

        final String propertyValue = property.getString();

        if ( maxLength != null && propertyValue != null )
        {
            validateValue( property, propertyValue.length() <= maxLength,
                           String.format( "Limit of %d character%s exceeded", maxLength, maxLength == 1 ? "" : "s" ) );
        }
    }
}
