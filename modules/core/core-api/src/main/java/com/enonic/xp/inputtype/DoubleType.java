package com.enonic.xp.inputtype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class DoubleType
    extends InputTypeBase
{
    public static final DoubleType INSTANCE = new DoubleType();

    private static final Logger LOG = LoggerFactory.getLogger( DoubleType.class );

    private DoubleType()
    {
        super( InputTypeName.DOUBLE );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newDouble( value.asDouble() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DOUBLE );
        validateMin( property, config );
        validateMax( property, config );
    }

    private void validateMin( final Property property, final InputTypeConfig config )
    {
        try
        {
            final Double min = getDoubleValueFromConfig( config, "min" );
            final Double value = property.getDouble();

            if ( min != null && value != null )
            {
                validateValue( property, value >= min, "The value cannot be less than " + min );
            }

        }
        catch ( ConvertException e )
        {
            LOG.warn( "Cannot convert 'min' config to Double", e );
        }
    }

    private void validateMax( final Property property, final InputTypeConfig config )
    {
        try
        {
            final Double max = getDoubleValueFromConfig( config, "max" );
            final Double value = property.getDouble();

            if ( max != null && value != null )
            {
                validateValue( property, value <= max, "The value cannot be greater than " + max );
            }
        }
        catch ( ConvertException e )
        {
            LOG.warn( "Cannot convert 'max' config to Double", e );
        }
    }

    private Double getDoubleValueFromConfig( final InputTypeConfig config, final String key )
    {
        return config.getProperty( key ).map( InputTypeProperty::getValue ).map( PropertyValue::asDouble ).orElse( null );
    }
}
