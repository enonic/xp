package com.enonic.xp.inputtype;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

@PublicApi
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
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();
        if ( defaultValue != null )
        {
            final Boolean withTimezone = useTimeZone( input.getInputTypeConfig() );

            Value value = withTimezone ? parseDateTime( defaultValue ) : parseLocalDateTime( defaultValue );

            if ( value != null )
            {
                return value;
            }

            final RelativeTime result = RelativeTimeParser.parse( defaultValue );
            if ( result != null )
            {
                final Instant instant = Instant.now().plus( result.getTime() );
                final Period period = result.getDate();

                final ZonedDateTime zonedDateTime = instant.atZone( ZoneId.systemDefault() ).
                    plusYears( period.getYears() ).
                    plusMonths( period.getMonths() ).
                    plusDays( period.getDays() ).
                    withNano( 0 );

                return withTimezone ?
                    ValueFactory.newDateTime( zonedDateTime.toInstant() ) :
                    ValueFactory.newLocalDateTime( zonedDateTime.toLocalDateTime() );
            }
            else
            {
                throw new IllegalArgumentException( "Invalid DateTime format: " + defaultValue );
            }
        }
        return super.createDefaultValue( input );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DATE_TIME, ValueTypes.LOCAL_DATE_TIME );
    }

    private Value parseDateTime( final String value )
    {
        try
        {
            return ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( value ) );
        }
        catch ( ValueTypeException e )
        {
            return null;
        }
    }

    private Value parseLocalDateTime( final String value )
    {
        try
        {
            return ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
        catch ( ValueTypeException e )
        {
            return null;
        }
    }
}
