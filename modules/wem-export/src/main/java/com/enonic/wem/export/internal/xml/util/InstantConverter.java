package com.enonic.wem.export.internal.xml.util;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.enonic.wem.export.ExportNodeException;

public class InstantConverter
{
    private static final int UTC_OFFSET = 0;

    public static XMLGregorianCalendar convertToXmlSerializable( final Instant instant )
    {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime( Date.from( instant ) );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( c );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    public static XMLGregorianCalendar convertToXmlSerializable( final LocalTime localTime )
    {
        final int ms = safeLongToInt( TimeUnit.NANOSECONDS.toMillis( localTime.getNano() ) );

        try
        {
            return DatatypeFactory.newInstance().
                newXMLGregorianCalendarTime( localTime.getHour(), localTime.getMinute(), localTime.getSecond(), ms, UTC_OFFSET );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }


    public static XMLGregorianCalendar convertToXmlSerializable( final LocalDate localDate )
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime( Date.valueOf( localDate ) );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ) + 1,
                                                                              cal.get( Calendar.DAY_OF_MONTH ), 0 );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    public static XMLGregorianCalendar convertToXmlSerializable( final LocalDateTime localDateTime )
    {
        GregorianCalendar c = new GregorianCalendar();
        //noinspection MagicConstant
        c.set( localDateTime.getYear(), localDateTime.getMonthValue() + 1, localDateTime.getDayOfMonth(), localDateTime.getHour(),
               localDateTime.getMinute(), localDateTime.getSecond() );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( c );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    private static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

}
