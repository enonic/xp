package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;

@Beta
final class DateTimeType
    extends InputTypeBase
{
    public final static DateTimeType INSTANCE = new DateTimeType();

    private DateTimeType()
    {
        super( InputTypeName.DATE_TIME );
    }

    private boolean useTimeZone( final InputTypeConfig config )
    {
        return config.getValue( "timezone", boolean.class, false );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        if ( useTimeZone( config ) )
        {
            return ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( value ) );
        }
        else
        {
            return ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultConfig )
    {
        final InputTypeProperty defaultProperty = defaultConfig.getProperty( "default" );
        if ( defaultProperty != null )
        {
            try
            {
                return ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( defaultProperty.getValue() ) );
            }
            catch ( ValueTypeException e )
            {
                try
                {
                    return ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( defaultProperty.getValue() ) );
                }
                catch ( ValueTypeException e1 )
                {
                    throw new IllegalArgumentException("Invalid DateTime format: " + defaultProperty.getValue());
                }
            }
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DATE_TIME, ValueTypes.LOCAL_DATE_TIME );
    }
}
