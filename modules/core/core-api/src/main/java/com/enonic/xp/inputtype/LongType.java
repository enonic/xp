package com.enonic.xp.inputtype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class LongType
    extends InputTypeBase
{
    public static final LongType INSTANCE = new LongType();

    private static final Logger LOG = LoggerFactory.getLogger( LongType.class );

    private LongType()
    {
        super( InputTypeName.LONG );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newLong( value.asLong() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateType( property, ValueTypes.LONG );
        validateMin( property, config );
        validateMax( property, config );
    }

    private void validateMin( final Property property, final GenericValue config )
    {
        try
        {
            final Long min = getLongValueFromConfig( config, "min" );
            final Long value = property.getLong();

            if ( min != null && value != null )
            {
                validateValue( property, value >= min, "The value cannot be less than " + min );
            }

        }
        catch ( ConvertException e )
        {
            LOG.warn( "Cannot convert 'min' config to Long", e );

        }
    }

    private void validateMax( final Property property, final GenericValue config )
    {
        try
        {
            final Long max = getLongValueFromConfig( config, "max" );
            final Long value = property.getLong();

            if ( max != null && value != null )
            {
                validateValue( property, value <= max, "The value cannot be greater than " + max );
            }
        }
        catch ( ConvertException e )
        {
            LOG.warn( "Cannot convert 'max' config to Long", e );
        }
    }

    private Long getLongValueFromConfig( final GenericValue config, final String key )
    {
        return config.optional( key ).map( GenericValue::asLong ).orElse( null );
    }
}
