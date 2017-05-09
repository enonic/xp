package com.enonic.xp.lib.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

public class ObjectToValueMapper
{
    public static Value map( final Object value )
    {
        if ( value instanceof Instant )
        {
            return ValueFactory.newDateTime( (Instant) value );
        }
        else if ( value instanceof GeoPoint )
        {
            return ValueFactory.newGeoPoint( (GeoPoint) value );
        }
        else if ( value instanceof Double )
        {
            return ValueFactory.newDouble( (Double) value );
        }
        else if ( value instanceof Float )
        {
            return ValueFactory.newDouble( ( (Float) value ).doubleValue() );
        }
        else if ( value instanceof Integer )
        {
            return ValueFactory.newLong( ( (Integer) value ).longValue() );
        }
        else if ( value instanceof Byte )
        {
            return ValueFactory.newLong( ( (Byte) value ).longValue() );
        }
        else if ( value instanceof Long )
        {
            return ValueFactory.newLong( (Long) value );
        }
        else if ( value instanceof Number )
        {
            return ValueFactory.newDouble( ( (Number) value ).doubleValue() );
        }
        else if ( value instanceof Boolean )
        {
            return ValueFactory.newBoolean( (Boolean) value );
        }
        else if ( value instanceof LocalDateTime )
        {
            return ValueFactory.newLocalDateTime( (LocalDateTime) value );
        }
        else if ( value instanceof LocalDate )
        {
            return ValueFactory.newLocalDate( (LocalDate) value );
        }
        else if ( value instanceof LocalTime )
        {
            return ValueFactory.newLocalTime( (LocalTime) value );
        }
        else if ( value instanceof Date )
        {
            return ValueFactory.newDateTime( ( (Date) value ).toInstant() );
        }
        else if ( value instanceof Reference )
        {
            return ValueFactory.newReference( (Reference) value );
        }
        else
        {
            return ValueFactory.newString( value.toString() );
        }
    }
}
