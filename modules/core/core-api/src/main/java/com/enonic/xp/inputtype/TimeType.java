package com.enonic.xp.inputtype;

import java.time.Instant;
import java.time.ZoneId;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;

final class TimeType
    extends InputTypeBase
{
    public final static TimeType INSTANCE = new TimeType();

    private TimeType()
    {
        super( InputTypeName.TIME );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) );
    }

    @Override
    public Value createDefaultValue( final InputTypeDefault defaultConfig )
    {
        final String defaultValue = defaultConfig.getRootValue();
        if ( defaultValue != null )
        {
            try
            {
                return ValueFactory.newLocalTime( ValueTypes.LOCAL_TIME.convert( defaultValue ) );
            }
            catch ( ValueTypeException e )
            {
                final RelativeTime result = RelativeTimeParser.parse( defaultValue );

                if ( result != null )
                {
                    final Instant instant = Instant.now().plus( result.getTime() );
                    return ValueFactory.newLocalTime( instant.atZone( ZoneId.systemDefault() ).toLocalTime() );
                }
                else
                {
                    throw new IllegalArgumentException( "Invalid Date format: " + defaultValue );
                }
            }
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LOCAL_TIME );
    }
}
