package com.enonic.wem.api.content.data.type;


import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.wem.api.content.data.Value;

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
    public Value ensureTypeOfValue( final Value value )
        throws InconvertibleValueException
    {
        return toDate( value );
    }

    public Value toDate( final Value value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else if ( value.isJavaType( String.class ) )
        {
            try
            {
                return newValue( FORMATTER.parseDateTime( (String) value.getObject() ).toDateMidnight() );
            }
            catch ( Exception e )
            {
                throw new InconvertibleValueException( value, this, e );
            }
        }
        else if ( value.isJavaType( Long.class ) )
        {
            final Long longValue = (Long) value.getObject();
            return newValue( new DateMidnight( longValue ) );
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
