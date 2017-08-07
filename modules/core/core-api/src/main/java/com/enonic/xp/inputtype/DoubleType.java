package com.enonic.xp.inputtype;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

final class DoubleType
    extends InputTypeBase
{
    public final static DoubleType INSTANCE = new DoubleType();

    private final static Logger LOG = LoggerFactory.getLogger( DoubleType.class );

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
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();
        if ( StringUtils.isNotEmpty( defaultValue ) )
        {
            return ValueFactory.newDouble( Double.valueOf( defaultValue ) );
        }
        return super.createDefaultValue( input );
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
            final Long min = config.getValue( "min", Long.class );
            final Long value = property.getLong();

            if ( min != null && value != null )
            {
                validateValue( property, value >= min, "The value cannot be less than " + min.toString() );
            }

        }
        catch ( ConvertException e )
        {
            LOG.warn("Cannot convert 'min' config to Double", e );
        }
    }

    private void validateMax( final Property property, final InputTypeConfig config )
    {
        try
        {
            final Long max = config.getValue( "max", Long.class );
            final Long value = property.getLong();

            if ( max != null && value != null )
            {
                validateValue( property, value <= max, "The value cannot be greater than " + max.toString() );
            }
        }
        catch ( ConvertException e )
        {
            LOG.warn("Cannot convert 'max' config to Double", e );
        }
    }
}
