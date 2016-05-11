package com.enonic.xp.inputtype;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;

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
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        if ( useTimeZone( config ) )
        {
            return ValueFactory.newDateTime( value.asInstant() );
        }
        else
        {
            return ValueFactory.newLocalDateTime( value.asLocalDateTime() );
        }
    }

    @Override
    public Value createDefaultValue( final InputTypeDefault defaultConfig )
    {
        final String defaultValue = defaultConfig.getRootValue();
        if ( defaultValue != null )
        {
            try
            {
                return ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( defaultValue ) );
            }
            catch ( ValueTypeException e )
            {
                try
                {
                    return ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( defaultValue ) );
                }
                catch ( ValueTypeException e1 )
                {
                    final RelativeTime result = RelativeTimeParser.parse( defaultValue );

                    if ( result != null )
                    {
                        final Instant instant = Instant.now().plus( result.getTime() );
                        final LocalDateTime localDateTime = instant.atZone( ZoneId.systemDefault() ).toLocalDateTime();

                        final Period period = result.getDate();

                        return ValueFactory.newLocalDateTime(
                            localDateTime.plusYears( period.getYears() ).
                                plusMonths( period.getMonths() ).
                                plusDays( period.getDays() )
                        );
                    }
                    else
                    {
                        throw new IllegalArgumentException( "Invalid Date format: " + defaultValue );
                    }
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
