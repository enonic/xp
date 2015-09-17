package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

public final class ValueFactory
{
    public static Value newDateTime( final Instant value )
    {
        return new DateTimeValue( value );
    }

    public static Value newLocalTime( final LocalTime value )
    {
        return new LocalTimeValue( value );
    }

    public static Value newLocalDateTime( final LocalDateTime value )
    {
        return new LocalDateTimeValue( value );
    }

    public static Value newLocalDate( final LocalDate value )
    {
        return new LocalDateValue( value );
    }

    public static Value newLong( final Long value )
    {
        return new LongValue( value );
    }

    public static Value newBoolean( final Boolean value )
    {
        return new BooleanValue( value );
    }

    public static Value newDouble( final Double value )
    {
        return new DoubleValue( value );
    }

    public static Value newString( final String value )
    {
        return new StringValue( value );
    }

    public static Value newXml( final String value )
    {
        return new XmlValue( value );
    }

    public static Value newGeoPoint( final GeoPoint value )
    {
        return new GeoPointValue( value );
    }

    public static Value newReference( final Reference value )
    {
        return new ReferenceValue( value );
    }

    public static Value newBinaryReference( final BinaryReference value )
    {
        return new BinaryReferenceValue( value );
    }

    public static Value newLink( final Link value )
    {
        return new LinkValue( value );
    }

    public static Value newPropertySet( final PropertySet value )
    {
        return new PropertySetValue( value );
    }
}
