package com.enonic.xp.inputtype;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;

abstract class NumberType
    extends InputTypeBase
{
    protected NumberType( final InputTypeName name )
    {
       super(name);
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateMin( property, config );
        validateMax( property, config );
    }

    private void validateMin( final Property property, final InputTypeConfig config )
    {
        try
        {
            final Double min = config.getValue( "min", Double.class );
            final Double value = property.getDouble();

            if ( min != null && value != null )
            {
                validateValue( property, value >= min, "The value cannot be less than " + min.toString() );
            }

        }
        catch ( ConvertException e )
        {
        }
    }

    private void validateMax( final Property property, final InputTypeConfig config )
    {
        try
        {
            final Double max = config.getValue( "max", Double.class );
            final Double value = property.getDouble();

            if ( max != null && value != null )
            {
                validateValue( property, value <= max, "The value cannot be greater than " + max.toString() );
            }
        }
        catch ( ConvertException e )
        {
        }
    }
}
