package com.enonic.wem.api.content.datatype;


import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class Date
    extends BaseDataType
{
    private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
        appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

    Date( int key )
    {
        super( key, JavaType.DATE );
    }

    @Override
    public String getIndexableString( final Object object )
    {
        return FORMATTER.print( (DateMidnight) object );
    }

    @Override
    public Object ensureTypeOfValue( final Object value )
    {
        return toDate( value );
    }

    public DateMidnight toDate( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return (DateMidnight) value;
        }
        else if ( value instanceof String )
        {
            try
            {
                return FORMATTER.parseDateTime( (String) value ).toDateMidnight();
            }
            catch ( Exception e )
            {
                throw new InconvertibleValueException( value, this, e );
            }
        }
        else if ( value instanceof Long )
        {
            return new DateMidnight( value );
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
