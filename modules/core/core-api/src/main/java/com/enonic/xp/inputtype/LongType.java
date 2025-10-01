package com.enonic.xp.inputtype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static com.google.common.base.Strings.isNullOrEmpty;

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
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newLong( value.asLong() );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();
        if ( !isNullOrEmpty( defaultValue ) )
        {
            return ValueFactory.newLong( Long.valueOf( defaultValue ) );
        }
        return super.createDefaultValue( input );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LONG );
        validateMin( property, config );
        validateMax( property, config );
    }

    private void validateMin( final Property property, final InputTypeConfig config )
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

    private void validateMax( final Property property, final InputTypeConfig config )
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

    private Long getLongValueFromConfig( final InputTypeConfig config, final String key )
    {
        return config.getProperty( key ).map( InputTypeProperty::getValue ).map( PropertyValue::asLong ).orElse( null );
    }
}
