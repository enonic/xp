package com.enonic.wem.core.content.datatype;


import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class Date
    extends AbstractDataType
{
    private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
        appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

    Date( int key )
    {
        super( key, JavaType.DATE, FieldTypes.DATE );
    }

    @Override
    public String getIndexableString( final Object object )
    {
        return FORMATTER.print( (DateMidnight) object );
    }

    @Override
    public String convertToString( final Object value )
    {
        DateMidnight date = (DateMidnight) value;
        return FORMATTER.print( date );
    }

    @Override
    public boolean isConvertibleTo( final JavaType type )
    {
        return type == JavaType.DATE || type == JavaType.STRING;
    }

    @Override
    public Object ensureType( final Object value )
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
                throw new InconvertibleException( value, this, e );
            }
        }
        else if ( value instanceof Long )
        {
            return new DateMidnight( value );
        }
        else
        {
            throw new InconvertibleException( value, this );
        }
    }
}
